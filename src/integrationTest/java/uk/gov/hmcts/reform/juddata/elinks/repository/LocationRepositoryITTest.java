package uk.gov.hmcts.reform.juddata.elinks.repository;

import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.AfterEach;
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
import uk.gov.hmcts.reform.juddata.configuration.EmailConfiguration;
import uk.gov.hmcts.reform.juddata.configuration.FeignConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = {"spring.config.location=classpath:application-integration-test.yml,"
        +"classpath:application-test.yml,"
        + "classpath:application-leaf-integration-test.yml"})
@MockEndpoints("log:*")
@ContextConfiguration(classes = {
        AzureBlobConfig.class,
        FeignConfiguration.class,
        EmailConfiguration.class

},
        initializers = ConfigDataApplicationContextInitializer.class)


@EnableAutoConfiguration

@EntityScan("uk.gov.hmcts.reform.elinks.domain")
@EnableJpaRepositories("uk.gov.hmcts.reform.elinks.repository")

@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
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

    @AfterEach
    void cleanUpData(){

        locationRepository.deleteAllInBatch();
    }
    @Test
    void test_save_location() {
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

    @Test
    void test_save_All_Locations_UpdateLocation() {

        Location locationOne = new Location();
        locationOne.setRegionId("1");
        locationOne.setRegionDescEn("National");

        Location locationTwo = new Location();
        locationTwo.setRegionId("1");
        locationTwo.setRegionDescEn("National England and Wales");


        List<Location> locations = List.of(locationOne,locationTwo);

        locationRepository.saveAll(locations);

        List<Location> result = locationRepository.findAll();
        //As default record is inserted through flyway script
        assertThat(result.size()).isEqualTo(2);

        assertThat(result.get(1).getRegionId()).isEqualTo(locationTwo.getRegionId());
        assertThat(result.get(1).getRegionDescEn()).isEqualTo(locationTwo.getRegionDescEn());
        assertThat(result.get(1).getRegionDescCy()).isBlank();

    }

}