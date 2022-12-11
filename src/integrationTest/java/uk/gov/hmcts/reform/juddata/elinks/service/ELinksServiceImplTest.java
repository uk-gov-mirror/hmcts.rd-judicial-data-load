package uk.gov.hmcts.reform.juddata.elinks.service;

import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.data.ingestion.configuration.AzureBlobConfig;
import uk.gov.hmcts.reform.elinks.configuration.ElinksFeignInterceptorConfiguration;
import uk.gov.hmcts.reform.elinks.configuration.FeignHeaderConfig;
import uk.gov.hmcts.reform.elinks.domain.Location;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.juddata.client.IdamClient;
import uk.gov.hmcts.reform.juddata.configuration.EmailConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.LOCATION_DATA_LOAD_SUCCESS;

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
        ELinksServiceImpl.class,
        ELinksServiceImplTestConfig.class

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
public class ELinksServiceImplTest {


    @Autowired
    LocationRepository locationRepository;

    @Mock
    ElinksFeignClient elinksFeignClientOne;

    @Autowired
    ELinksServiceImpl eLinksServiceImpl;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eLinksServiceImpl, "elinksFeignClient",
                elinksFeignClientOne
        );
    }

    @Test
    void test_elinksService_loaded() {
        assertThat(locationRepository).isNotNull();
        assertThat(eLinksServiceImpl).isNotNull();
    }



    @Test
    void test_elinksService_load_location_return_staus_200() {

        List<Location> locations = getLocationsData();

        when(elinksFeignClientOne.getLocationDetails()).thenReturn(locations);

        ResponseEntity<Object> responseEntity = eLinksServiceImpl.retrieveLocation();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getBody()).isEqualTo(LOCATION_DATA_LOAD_SUCCESS);
    }

    @Test
    void test_elinksService_load_location_return_staus_200_validate_data() {

        List<Location> locations = getLocationsData();

        when(elinksFeignClientOne.getLocationDetails()).thenReturn(locations);

        ResponseEntity<Object> responseEntity = eLinksServiceImpl.retrieveLocation();

        List<Location> result = locationRepository.findAllById(List.of("1","2","3"));

        assertThat(result.size()).isEqualTo(3);

        assertThat(result.get(0).getRegionId()).isEqualTo("1");
        assertThat(result.get(0).getRegionDescEn()).isEqualTo("National");
        assertThat(result.get(0).getRegionDescCy()).isBlank();

        assertThat(result.get(1).getRegionId()).isEqualTo("2");
        assertThat(result.get(1).getRegionDescEn()).isEqualTo("National England and Wales");
        assertThat(result.get(1).getRegionDescCy()).isBlank();

    }

    private List<Location> getLocationsData(){

        List<Location> locations = new ArrayList<>();
        Location locationOne = new Location();
        locationOne.setRegionId("1");
        locationOne.setRegionDescEn("National");

        Location locationTwo = new Location();
        locationTwo.setRegionId("2");
        locationTwo.setRegionDescEn("National England and Wales");


        Location locationThree = new Location();
        locationThree.setRegionId("3");
        locationThree.setRegionDescEn("Taylor House (London)");
        locations.add(locationOne);
        locations.add(locationTwo);
        locations.add(locationThree);

        return locations;

    }

}
