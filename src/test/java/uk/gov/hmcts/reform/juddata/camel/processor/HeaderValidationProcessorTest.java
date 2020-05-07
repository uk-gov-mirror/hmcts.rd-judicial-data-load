package uk.gov.hmcts.reform.juddata.camel.processor;

import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.ROUTE_DETAILS;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import uk.gov.hmcts.reform.juddata.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.juddata.camel.helper.JrdTestSupport;
import uk.gov.hmcts.reform.juddata.camel.route.beans.RouteProperties;

@RunWith(CamelSpringBootRunner.class)
public class HeaderValidationProcessorTest extends CamelTestSupport {
    public static final String FILE_JUDICIAL_APPOINTMENTS_INVALIDHEADER_CSV = "src/test/resources/sourceFiles/judicial_appointments_invalidheader.csv";
    @Mock
    ApplicationContext applicationContext;
    @Mock
    CamelContext camelContext;
    @Mock
    JdbcTemplate jdbcTemplate;
    @Mock
    PlatformTransactionManager platformTransactionManager;
    @InjectMocks
    HeaderValidationProcessor headerValidationProcessor;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        RouteProperties routeProperties = JrdTestSupport.createRoutePropertiesMock();
        Exchange exchange = ExchangeBuilder.anExchange(new DefaultCamelContext())
            .withBody(JrdTestSupport.getInputStreamOfFile(FILE_JUDICIAL_APPOINTMENTS_INVALIDHEADER_CSV))
            .withHeader(ROUTE_DETAILS,routeProperties)
            .build();
        when(camelContext.getGlobalOptions()).thenReturn(JrdTestSupport.createMockGlobalOptions(exchange));
        exceptionRule.expect(RouteFailedException.class);
        exceptionRule.expectMessage("Header count mismatch ::Test.csv");
        headerValidationProcessor.process(exchange);
    }
}
