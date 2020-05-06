package uk.gov.hmcts.reform.juddata.camel.processor;

import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.ROUTE_DETAILS;

import com.opencsv.CSVReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.hmcts.reform.juddata.camel.route.beans.RouteProperties;
import uk.gov.hmcts.reform.juddata.camel.util.HeaderUtil;


@Component
public class HeaderValidationProcessor implements Processor {

    @Autowired
    HeaderUtil headerUtil;

    @Override
    public void process(Exchange exchange) throws Exception {

        RouteProperties routeProperties = (RouteProperties) exchange.getIn().getHeader(ROUTE_DETAILS);
        String csv = exchange.getIn().getBody(String.class);
        CSVReader reader = new CSVReader(new StringReader(csv));
        String[] header = reader.readNext();
        String exceptionMsg = headerUtil.getInvalidJrdHeader(Arrays.asList(header),routeProperties.getBinder());
        if (!exceptionMsg.isEmpty()) {
            headerUtil.checkHeader(exchange, routeProperties, exceptionMsg);
        }
        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(Charset.forName("UTF-8")));
        exchange.getMessage().setBody(inputStream);
    }
}
