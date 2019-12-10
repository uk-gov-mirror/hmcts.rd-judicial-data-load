package uk.gov.hmcts.reform.judicialapi.camel;

import com.microsoft.azure.storage.blob.ListBlobItem;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.language.bean.BeanLanguage;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class BlobFileProcessorRoute extends SpringRouteBuilder {

    private static String getListOfBlobfiles = "azure-blob://rddemo/jrdtest?credentials=#credsreg&operation=listBlobs&synchronous=true&exchangePattern=OutOnly&useFlatListing=true";

    private static String blobUrl = "azure-blob://rddemo%s?credentials=#credsreg";

    @Override
    public void configure() throws Exception {

        restConfiguration().component("restlet").host("0.0.0.0").port("9999");
        rest().get("/dataLoad/jrdTest").to("direct:JrdTest");

        Processor filterDataLoadFiles = new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Iterator<ListBlobItem> it = ((Iterable<ListBlobItem>) exchange.getIn().getBody()).iterator();
                List<String> filteredList = new ArrayList<>();
                while (it.hasNext()) {
                    String dataLoadFileName = it.next().getStorageUri().getPrimaryUri().getPath();
                    if (dataLoadFileName.endsWith(".csv")) {
                        filteredList.add(dataLoadFileName);
                    }
                }
                exchange.getIn().setBody(filteredList.iterator());
            }
        };

        from("direct:JrdTest")
          .to(getListOfBlobfiles)
            .process(filterDataLoadFiles)
                .loopDoWhile(simple("${body.hasNext()}"))
                    .setHeader("dataLoadFileName",   simple("${body.next()}"))
                    .choice()
                        .when(simple("${header.dataLoadFileName} contains 'Locations.csv'"))
                             .to("seda:locationsRoute")
                        .when(simple("${header.dataLoadFileName} contains 'Personal.csv'"))
                            .to("seda:personalsRoute")
                        .when(simple("${header.dataLoadFileName} contains 'judicial'"))
                            .to("seda:judicialRoute")
                        .when(simple("${header.dataLoadFileName} contains 'Roles.csv'"))
                            .to("seda:rolesRoute")
                        .otherwise()
                            .to("seda:routeException")
                    .endChoice()
                .end() //end loop
           .end()
              .onCompletion()
              .log("complete")
        .end();

        // G8CA9F - 180


        /*** Individual data loads ****/
        from("seda:locationsRoute")
                .log("reached locations route with ${header.dataLoadFileName} and body is ${body}").end();

        from("seda:personalsRoute")
                .log("reached personal route with ${header.dataLoadFileName}").end();

        from("seda:rolesRoute")
                .log("reached roles route with ${header.dataLoadFileName}").end();

        from("seda:judicialRoute")
                .log("reached judicial route with ${header.dataLoadFileName}").end();

        from("seda:routeException")
                .log("reached No data load route exception ${header.dataLoadFileName}").end();
        /** end *******/

    }

    public static String blobUrlBean(String filename) {
        return String.format(blobUrl, filename);
    }
}