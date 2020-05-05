package uk.gov.hmcts.reform.juddata.camel.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.SCHEDULER_NAME;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.SCHEDULER_START_TIME;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.ExchangeTestSupport;
import org.apache.camel.test.spring.CamelSpringRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import uk.gov.hmcts.reform.juddata.camel.binder.JudicialUserProfile;
import uk.gov.hmcts.reform.juddata.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.juddata.camel.route.beans.RouteProperties;

@RunWith(CamelSpringRunner.class)
@Configuration()
@ContextConfiguration(classes = {HeaderUtilTest.class,DataLoadUtil.class})
public class HeaderUtilTest extends ExchangeTestSupport {
    @Mock
    ApplicationContext applicationContext;
    @Mock
    CamelContext camelContext;

    @InjectMocks
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataLoadUtil dataLoadUtil;

    @Value("${judicialUserProfile.header}")
    private String header;

    private JdbcTemplate mockJdbcTemplate = mock(JdbcTemplate.class);

    PlatformTransactionManager platformTransactionManager = mock(PlatformTransactionManager.class);

    TransactionStatus transactionStatus = mock(TransactionStatus.class);
    Map readYmlAsMap;


    @Value("${Scheduler-insert-sql}")
    private String schedulerInsertSql;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setField(HeaderUtil.class, "jdbcTemplate", mockJdbcTemplate);
        setField(HeaderUtil.class, "platformTransactionManager", platformTransactionManager);
        readYmlAsMap = HeaderUtil.readYmlAsMap("test-header.yaml");
    }

    @Test(expected = RouteFailedException.class)
    public void testCheckHeader() throws Exception {
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setFileName("Test.csv");
        Exchange exchange = ExchangeBuilder.anExchange(new DefaultCamelContext())
            .withBody(" ")
            .build();
        Map<String, String> globalOptions = camelContext.getGlobalOptions();
        globalOptions.put(SCHEDULER_NAME, "judicial_leaf_scheduler");
        globalOptions.put(SCHEDULER_START_TIME, String.valueOf(new Date().getTime()));
        exchange.getContext().setGlobalOptions(globalOptions);
        setField(HeaderUtil.class, "camelContext", exchange.getContext());

        when(mockJdbcTemplate.update((PreparedStatementCreator) any(),any())).thenReturn(1);
        when(platformTransactionManager.getTransaction(any())).thenReturn(transactionStatus);
        doNothing().when(platformTransactionManager).commit(transactionStatus);

        HeaderUtil.checkHeader(exchange, routeProperties, "exceptionMsg");

        assertNull(routeProperties.getBinder());
        assertEquals("Test.csv", routeProperties.getFileName());
        assertEquals("judicial_leaf_scheduler", ((java.lang.String)globalOptions.get("SchedulerName")));


    }

    @Test
    public void testGetJrdHeaderValue() throws Exception {
        String  header = (String) ((Map) readYmlAsMap.get("judicialUserProfile")).get("header");
        List<String> headers = Arrays.asList(header.split(","));
        String result = HeaderUtil.getInvalidJrdHeader(JudicialUserProfile.class, headers, "judicialUserProfile");
        assertEquals("", result);

    }

    @Test
    public void testGetJrdHeaderValueMoreHeader() throws Exception {
        String  header = (String) ((Map) readYmlAsMap.get("judicialUserProfile")).get("extra-header");
        List<String> headers = Arrays.asList(header.split(","));
        String result = HeaderUtil.getInvalidJrdHeader(JudicialUserProfile.class, headers, "judicialUserProfile");
        assertEquals("Invalid column(s) : [dfads]. Please remove invalid column(s) from file.", result);

    }

    @Test
    public void testGetJrdHeaderValueLessHeader() throws Exception {
        String  header = (String) ((Map) readYmlAsMap.get("judicialUserProfile")).get("less-header");
        List<String> headers = Arrays.asList(header.split(","));
        String result = HeaderUtil.getInvalidJrdHeader(JudicialUserProfile.class, headers, "judicialUserProfile");
        assertEquals("Missing column(s) : [elinks_id]. Please add column(s) from file.", result);
    }

    @Test
    public void testGetJrdHeaderValueDifferentOrdee() throws Exception {
        String  header = (String) ((Map) readYmlAsMap.get("judicialUserProfile")).get("unordered-header");
        List<String> headers = Arrays.asList(header.split(","));
        String result = HeaderUtil.getInvalidJrdHeader(JudicialUserProfile.class, headers, "judicialUserProfile");
        assertEquals("", result);
    }

    @Test
    public void testreadYmlAsMap() throws Exception {
        Map readYmlAsMap = HeaderUtil.readYmlAsMap("header-mapping.yaml");
        String judicialUserProfile = (String) readYmlAsMap.get("judicialUserProfile");
    }
}