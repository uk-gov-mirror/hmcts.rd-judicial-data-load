package uk.gov.hmcts.reform.juddata;

import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.data.ingestion.configuration.AzureBlobConfig;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.juddata.config.LocationConfig;
import uk.gov.hmcts.reform.juddata.configuration.EmailConfiguration;
import uk.gov.hmcts.reform.juddata.configuration.FeignConfiguration;

import static org.assertj.core.api.Assertions.assertThat;




@TestPropertySource(properties = {"spring.config.location=classpath:application-integration-test.yml,"
        +"classpath:application-test.yml,"
        + "classpath:application-leaf-integration-test.yml"})
@MockEndpoints("log:*")
@ContextConfiguration(classes = {
        AzureBlobConfig.class,
        FeignConfiguration.class,
        EmailConfiguration.class,
        LocationConfig.class

    },
        initializers = ConfigDataApplicationContextInitializer.class)

//@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)

@EnableAutoConfiguration

@EntityScan("uk.gov.hmcts.reform.elinks.domain")
@EnableJpaRepositories("uk.gov.hmcts.reform.elinks.repository")

//@EnableTransactionManagement
//@SqlConfig(dataSource = "dataSource", transactionManager = "txManager",
//        transactionMode = SqlConfig.TransactionMode.ISOLATED)
//@SpringBootTest
//@EnableFeignClients(clients = {IdamClient.class, IdamApi.class})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
//(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SuppressWarnings("all")
public class LocationRepositoryITTest  {



    @Autowired
    LocationRepository locationRepository;

    @Test
    void test_locationRepository_loaded() {
        assertThat(locationRepository).isNotNull();
    }

}
