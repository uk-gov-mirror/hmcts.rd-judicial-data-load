package uk.gov.hmcts.reform.juddata.elinks.controller;

import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.data.ingestion.configuration.AzureBlobConfig;
import uk.gov.hmcts.reform.elinks.ELinksController;
import uk.gov.hmcts.reform.elinks.configuration.ElinksFeignInterceptorConfiguration;
import uk.gov.hmcts.reform.elinks.configuration.FeignHeaderConfig;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.juddata.client.IdamClient;
import uk.gov.hmcts.reform.juddata.config.LocationConfig;
import uk.gov.hmcts.reform.juddata.configuration.EmailConfiguration;
import uk.gov.hmcts.reform.juddata.elinks.service.ELinksServiceImplTestConfig;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.config.location=classpath:application-integration-test.yml,"
        +"classpath:application-test.yml,"
        + "classpath:application-leaf-integration-test.yml"})
@MockEndpoints("log:*")


@ContextConfiguration(classes = {
        AzureBlobConfig.class,
        FeignHeaderConfig.class,
        ElinksFeignInterceptorConfiguration.class,
        FeignHeaderConfig.class,

        EmailConfiguration.class,
        LocationConfig.class,
        ELinksServiceImpl.class,
        ELinksControllerTestConfig.class,
        ELinksController.class

},
        initializers = ConfigDataApplicationContextInitializer.class)

//@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)

@EnableAutoConfiguration
//@ComponentScan(basePackages = {"uk.gov.hmcts.reform.elinks"})
@EntityScan("uk.gov.hmcts.reform.elinks.domain")
@EnableJpaRepositories("uk.gov.hmcts.reform.elinks.repository")

//@ImportAutoConfiguration({FeignAutoConfiguration.class})
//@EnableFeignClients(clients = {ElinksFeignClient.class, IdamClient.class, IdamApi.class})
//@EnableFeignClients(basePackages = {"uk.gov.hmcts.reform.elinks.feign"})
//@EnableTransactionManagement
//@SqlConfig(dataSource = "dataSource", transactionManager = "txManager",
//        transactionMode = SqlConfig.TransactionMode.ISOLATED)
//@SpringBootTest
@EnableFeignClients(clients = {ElinksFeignClient.class,IdamClient.class, IdamApi.class})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
//(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SuppressWarnings("all")
public class ELinksControllerITTest {

    @Autowired
    ELinksController eLinksController;

    @Test
    void test_elinksController_loaded() {
        assertThat(eLinksController).isNotNull();
    }
}
