package uk.gov.hmcts.reform.judicialapi.camel;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileConverter;
import org.springframework.stereotype.Component;

@Component
public class JudicialUserFileProcessorRoute extends RouteBuilder {


    public static final String ROUTE_NAME = "MYROUTE";


    @Override
    public void configure() throws Exception {

               //  from("azure-blob://rddemo/jrdtest/judicial_userprofile.csv?credentials=#credsreg")
                         // from("timer:hello?repeatCount=1")
                        // from("azure-blob://rddemo/jrdtest/jrd1.csv?credentials=#credsreg&operation=deleteBlob")
                        // .to("azure-blob://rddemo/jrd-archive/judicial_userprofile.csv?credentials=#credsreg&operation=updateBlockBlob")
             /*    from("file://blobdirectory?noop=true")
                         .to("log:test?showAll=true")
              //  .convertBodyTo(java.lang.class)
                         .process(new Processor() {
                             @Override
                             public void process(Exchange exchange) throws Exception {
                                 Object file = exchange.getIn().getMandatoryBody();

                                 exchange.getOut().setBody(
                                         GenericFileConverter.genericFileToInputStream(
                                                 (GenericFile<?>) file, exchange));
                             }
                         })
                .to("azure-blob://rddemo/jrdtest/blob1?credentials=#credsreg")
                .to("log:test?showAll=true");*/

             /*

                from("azure-blob://rddemo/jrdtest/judicial_userprofile.csv?credentials=#credsreg")
                 .to("file://blobdirectory?noop=true").end();

                from("file://blobdirectory?noop=true")
                .onCompletion().log("CSV data  processing finished").end()

                . split(body().tokenize("\n",1,true)).streaming()
                .unmarshal().csv()

               // .unmarshal().bindy(BindyType.Csv, JrdCsvDataMapper.class)
                .log("Processing CSV data -- 2 ${body}")
                //  .to("bean:myCsvHandler?method=doHandleCsvData");
                //.to("mock:daltons")
                .split(body())
                // .process(new GetRecordsProcess())
                .log("Processing CSV data ---3 ---- ${body}")
               // .to //("log:test?level=DEBUG")
                .to("sql:insert into judicial_user (sno,firstName,LastName,Circuit,Area) values(#, #, #, #, #)?dataSource=dataSource")
                .to ("log:test?showAll=true");


              */

    }

}
