package uk.gov.hmcts.reform.judicialapi;

import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaAuditing
@EnableJpaRepositories
@EnableRetry
@SpringBootApplication
@EnableScheduling
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class JudicialApplication {



    @SuppressWarnings({"unchecked", "deprecated"})
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(JudicialApplication.class, args);
        SimpleRegistry myregistry = new SimpleRegistry();
        CamelContext mycontext = new DefaultCamelContext(myregistry);
        // String url = "jdbc:postgresql://rd-judicial-db:5456/dbjuddata";
        myregistry.put("credsreg", new StorageCredentialsBean().credentials());
        mycontext.addRoutes
            (new RouteBuilder()
         {
            @Override
            public void configure()
            {

                from("azure-blob://rddemo/jrdtest/simple.csv?credentials=#credsreg")
                        .to("file://blobdirectory");

            }

        } );
		mycontext.start();
}

static class StorageCredentialsBean
    {
        StorageCredentials credentials() throws Exception
        {
            try
            {
                return new StorageCredentialsAccountAndKey("rddemo", "Vjmr3Yk0DUrygumP7FKJ2eUiOyhWvZ5XczDMk13J+m9vtk2IOXEcOlfjlcTaHDb5mHwAsq7qOYf3GtxEo/uFIA==");
            } catch(Exception ex)
            {
                throw ex;
            }
        }
    }
}
