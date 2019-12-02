package uk.gov.hmcts.reform.judicialapi.camel;


import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class JrdFileProcessorRoute extends RouteBuilder {


    public static final String ROUTE_NAME = "MYROUTE";


    @Override
    public void configure() throws Exception {

                /*  from("file://blobdirectory?noop=true")
                .onCompletion().log("CSV data  processing finished").end()
                .unmarshal().csv()
                .log("Processing CSV data -- 2 ${body}")
                 //  .to("bean:myCsvHandler?method=doHandleCsvData");
                .to("mock:daltons")
                .split(body())
                // .process(new GetRecordsProcess())
                .log("Processing CSV data ---3 ---- ${body}");*/

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
                .to ("log:test?showAll=true")
                .end();


    }

}
