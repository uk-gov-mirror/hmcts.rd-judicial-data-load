package uk.gov.hmcts.reform.juddata.camel.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import uk.gov.hmcts.reform.juddata.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.juddata.camel.helper.JrdTestSupport;
import uk.gov.hmcts.reform.juddata.camel.route.beans.RouteProperties;


import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.ROUTE_DETAILS;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.SCHEDULER_NAME;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.SCHEDULER_START_TIME;

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
        String routHeader="elinks_id,Personal_Code,Title,Known_As,Surname,Full_Name,Post_Nominals,Contract_Type_Id,Work_Pattern,Email_Id,Joining_Date,Last_Working_Date,Active_Flag,Extracted_Date";
        RouteProperties routeProperties = JrdTestSupport.createRoutePropertiesMock();
        routeProperties.setRouteHeader(routHeader);
        boolean ss = new File("").exists();
        routeProperties.setRouteHeader(routHeader);
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

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme