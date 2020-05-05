package uk.gov.hmcts.reform.juddata.camel.processor;

import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.ROUTE_DETAILS;

import com.opencsv.CSVReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import uk.gov.hmcts.reform.juddata.camel.route.beans.RouteProperties;
import uk.gov.hmcts.reform.juddata.camel.util.HeaderUtil;

@Component
public class HeaderValidationProcessor implements Processor {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    CamelContext camelContext;

    @Autowired
    @Qualifier("springJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Value("${invalid-header-sql}")
    String invalidHeaderSql;

    @Autowired
    @Qualifier("springJdbcTransactionManager")
    PlatformTransactionManager platformTransactionManager;

    String exceptionMsg = "Mismatch headers in csv for ::";

    @Override
    public void process(Exchange exchange) throws Exception {

        RouteProperties routeProperties = (RouteProperties) exchange.getIn().getHeader(ROUTE_DETAILS);
        String csv = exchange.getIn().getBody(String.class);
        CSVReader reader = new CSVReader(new StringReader(csv));
        String[] header = reader.readNext();
        String exceptionMsg = HeaderUtil.getInvalidJrdHeader(applicationContext.getBean(routeProperties.getBinder()).getClass(),Arrays.asList(header),routeProperties.getBinder());
        if (!exceptionMsg.isEmpty()) {
            HeaderUtil.checkHeader(exchange, routeProperties, exceptionMsg);
        }

        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(Charset.forName("UTF-8")));

        exchange.getMessage().setBody(inputStream);
    }

}
