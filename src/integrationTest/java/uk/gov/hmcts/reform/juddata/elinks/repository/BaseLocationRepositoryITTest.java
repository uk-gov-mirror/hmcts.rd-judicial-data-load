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
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.elinks.domain.Location;
import uk.gov.hmcts.reform.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.elinks.response.BaseLocationResponse;
import uk.gov.hmcts.reform.juddata.configuration.EmailConfiguration;
import uk.gov.hmcts.reform.juddata.configuration.FeignConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = {"spring.config.location=classpath:application-integration-test.yml,"
        + "classpath:application-test.yml,"
        + "classpath:application-leaf-integration-test.yml"})
@MockEndpoints("log:*")
@ContextConfiguration(classes = {
        AzureBlobConfig.class,
        FeignConfiguration.class,
        EmailConfiguration.class},
        initializers = ConfigDataApplicationContextInitializer.class)


@EnableAutoConfiguration

@EntityScan("uk.gov.hmcts.reform.elinks.domain")
@EnableJpaRepositories("uk.gov.hmcts.reform.elinks.repository")

@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SuppressWarnings("AbbreviationAsWordInName")
class BaseLocationRepositoryITTest {

    @Autowired
    BaseLocationRepository baseLocationRepository;

    @Test
    void test_locationRepository_loaded() {
        assertThat(baseLocationRepository).isNotNull();
    }

    @AfterEach
    void cleanUpData() {

        baseLocationRepository.deleteAllInBatch();
    }

    @Test
    void test_save_baseLocation() {
        BaseLocation baseLocationOne = getBaseLocationEntityList().get(0);

        baseLocationRepository.save(baseLocationOne);

        BaseLocation result = baseLocationRepository.getOne(baseLocationOne.getBaseLocationId());

        assertThat(result.getBaseLocationId()).isEqualTo(baseLocationOne.getBaseLocationId());
        assertThat(result.getCourtName()).isEqualTo(baseLocationOne.getCourtName());
        assertThat(result.getCourtType()).isEqualTo(baseLocationOne.getCourtType());
        assertThat(result.getCircuit()).isEqualTo(baseLocationOne.getCircuit());
        assertThat(result.getAreaOfExpertise()).isEqualTo(baseLocationOne.getAreaOfExpertise());

    }

    @Test
    void test_save_All_BaseLocations() {

        BaseLocation baseLocationOne = getBaseLocationEntityList().get(0);
        BaseLocation baseLocationTwo = getBaseLocationEntityList().get(1);


        List<BaseLocation> baseLocations = List.of(baseLocationOne,baseLocationTwo);

        baseLocationRepository.saveAll(baseLocations);

        List<BaseLocation> result = baseLocationRepository.findAllById(List.of("1","2"));

        assertThat(result.size()).isEqualTo(2);


        assertThat(result.get(0).getBaseLocationId()).isEqualTo(baseLocationOne.getBaseLocationId());
        assertThat(result.get(0).getCourtName()).isEqualTo(baseLocationOne.getCourtName());
        assertThat(result.get(0).getCourtType()).isEqualTo(baseLocationOne.getCourtType());
        assertThat(result.get(0).getCircuit()).isEqualTo(baseLocationOne.getCircuit());
        assertThat(result.get(0).getAreaOfExpertise()).isEqualTo(baseLocationOne.getAreaOfExpertise());


        assertThat(result.get(1).getBaseLocationId()).isEqualTo(baseLocationTwo.getBaseLocationId());
        assertThat(result.get(1).getCourtName()).isEqualTo(baseLocationTwo.getCourtName());
        assertThat(result.get(1).getCourtType()).isEqualTo(baseLocationTwo.getCourtType());
        assertThat(result.get(1).getCircuit()).isEqualTo(baseLocationTwo.getCircuit());
        assertThat(result.get(1).getAreaOfExpertise()).isEqualTo(baseLocationTwo.getAreaOfExpertise());
    }

    @Test
    void test_save_All_BaseLocations_UpdateLocation() {

        Location locationOne = new Location();
        locationOne.setRegionId("1");
        locationOne.setRegionDescEn("National");

        Location locationTwo = new Location();
        locationTwo.setRegionId("1");
        locationTwo.setRegionDescEn("National England and Wales");


        BaseLocation baseLocationOne = getBaseLocationEntityList().get(0);

        BaseLocation baseLocationTwo = new BaseLocation();

        baseLocationTwo.setBaseLocationId("1");
        baseLocationTwo.setCourtName("Aldridge and Brownhills");
        baseLocationTwo.setCourtType("Nottinghamshire");
        baseLocationTwo.setCircuit("Nottinghamshire");
        baseLocationTwo.setAreaOfExpertise("LJA");


        List<BaseLocation> baseLocations = List.of(baseLocationOne,baseLocationTwo);

        baseLocationRepository.saveAll(baseLocations);

        List<BaseLocation> result = baseLocationRepository.findAll();

        assertThat(result.size()).isEqualTo(1);

        assertThat(result.get(0).getBaseLocationId()).isEqualTo(baseLocationTwo.getBaseLocationId());
        assertThat(result.get(0).getCourtName()).isEqualTo(baseLocationTwo.getCourtName());
        assertThat(result.get(0).getCourtType()).isEqualTo(baseLocationTwo.getCourtType());
        assertThat(result.get(0).getCircuit()).isEqualTo(baseLocationTwo.getCircuit());
        assertThat(result.get(0).getAreaOfExpertise()).isEqualTo(baseLocationTwo.getAreaOfExpertise());

    }

    private List<BaseLocation> getBaseLocationEntityList() {


        BaseLocation baseLocationOne = new BaseLocation();
        baseLocationOne.setBaseLocationId("1");
        baseLocationOne.setCourtName("National");
        baseLocationOne.setCourtType("Old Gwynedd");
        baseLocationOne.setCircuit("Gwynedd");
        baseLocationOne.setAreaOfExpertise("LJA");


        BaseLocation baseLocationTwo = new BaseLocation();
        baseLocationTwo.setBaseLocationId("2");
        baseLocationTwo.setCourtName("Aldridge and Brownhills");
        baseLocationTwo.setCourtType("Nottinghamshire");
        baseLocationTwo.setCircuit("Nottinghamshire");
        baseLocationTwo.setAreaOfExpertise("LJA");



        List<BaseLocation> baseLocations = new ArrayList<>();

        baseLocations.add(baseLocationOne);
        baseLocations.add(baseLocationTwo);

        return baseLocations;

    }

}