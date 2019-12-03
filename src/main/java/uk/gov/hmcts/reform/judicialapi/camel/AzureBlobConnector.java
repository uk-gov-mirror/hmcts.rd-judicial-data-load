
package uk.gov.hmcts.reform.judicialapi.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class AzureBlobConnector extends RouteBuilder {

        @Override
        public void configure()
        {
             //from ( "quartz://loop?cron=0+*/10+*+*+*+?")
             // from ("timer:hello?repeatCount=1")
              /*from("azure-blob://rddemo/jrdtest/simple.csv?credentials=#credsreg")
                    .to("file://blobdirectory");*/

             // from("direct:route1").recipientList().;

             //  from("direct:route1").recipientList("");
        }

}


