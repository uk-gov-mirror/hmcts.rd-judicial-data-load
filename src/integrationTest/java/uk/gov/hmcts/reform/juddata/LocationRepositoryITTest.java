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
import uk.gov.hmcts.reform.elinks.domain.Location;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.juddata.config.LocationConfig;
import uk.gov.hmcts.reform.juddata.configuration.EmailConfiguration;
import uk.gov.hmcts.reform.juddata.configuration.FeignConfiguration;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void test_save_Location() {
        Location location = new Location();
        location.setRegionId("2");
        location.setRegionDescEn("National England and Wales");
        locationRepository.save(location);

        Location result = locationRepository.getOne(location.getRegionId());

        assertThat(result.getRegionId()).isEqualTo(location.getRegionId());
        assertThat(result.getRegionDescEn()).isEqualTo(location.getRegionDescEn());
        assertThat(result.getRegionDescCy()).isBlank();
    }

    @Test
    void test_save_All_Locations() {

        Location locationOne = new Location();
        locationOne.setRegionId("1");
        locationOne.setRegionDescEn("National");

        Location locationTwo = new Location();
        locationTwo.setRegionId("2");
        locationTwo.setRegionDescEn("National England and Wales");


        Location locationThree = new Location();
        locationThree.setRegionId("3");
        locationThree.setRegionDescEn("Taylor House (London)");


        List<Location> locations = List.of(locationOne,locationTwo,locationThree);

        locationRepository.saveAll(locations);

        List<Location> result = locationRepository.findAllById(List.of("1","2","3"));

        assertThat(result.size()).isEqualTo(3);

        assertThat(result.get(0).getRegionId()).isEqualTo(locationOne.getRegionId());
        assertThat(result.get(0).getRegionDescEn()).isEqualTo(locationOne.getRegionDescEn());
        assertThat(result.get(0).getRegionDescCy()).isBlank();

        assertThat(result.get(2).getRegionId()).isEqualTo(locationThree.getRegionId());
        assertThat(result.get(2).getRegionDescEn()).isEqualTo(locationThree.getRegionDescEn());
        assertThat(result.get(2).getRegionDescCy()).isBlank();
    }


}
