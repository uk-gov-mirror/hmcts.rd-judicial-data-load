package uk.gov.hmcts.reform.juddata.elinks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
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
import uk.gov.hmcts.reform.elinks.response.ElinkLocationResponse;
import uk.gov.hmcts.reform.elinks.response.LocationResponse;
import uk.gov.hmcts.reform.elinks.service.impl.ELinksServiceImpl;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.juddata.client.IdamClient;
import uk.gov.hmcts.reform.juddata.configuration.EmailConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.LOCATION_DATA_LOAD_SUCCESS;

@TestPropertySource(properties = {"spring.config.location=classpath:application-integration-test.yml,"
        + "classpath:application-test.yml,"
        + "classpath:application-leaf-integration-test.yml"})
@MockEndpoints("log:*")


@ContextConfiguration(classes = {
        AzureBlobConfig.class,
        FeignHeaderConfig.class,
        ElinksFeignInterceptorConfiguration.class,
        FeignHeaderConfig.class,

        EmailConfiguration.class,
        ELinksServiceImpl.class,
        ELinksServiceImplTestConfig.class},
        initializers = ConfigDataApplicationContextInitializer.class)


@EnableAutoConfiguration
@EntityScan("uk.gov.hmcts.reform.elinks.domain")
@EnableJpaRepositories("uk.gov.hmcts.reform.elinks.repository")

@EnableFeignClients(clients = {ElinksFeignClient.class,IdamClient.class, IdamApi.class})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SuppressWarnings({"AbbreviationAsWordInName","MemberName"})
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
    void test_elinksService_load_location_return_staus_200() throws IOException {

        List<LocationResponse> locations = getLocationResponseData();


        ElinkLocationResponse elinkLocationResponse = new ElinkLocationResponse();
        elinkLocationResponse.setResults(locations);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClientOne.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());


        ResponseEntity<String> responseEntity = eLinksServiceImpl.retrieveLocation();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getBody()).isEqualTo(LOCATION_DATA_LOAD_SUCCESS);
    }

    @Test
    void test_elinksService_load_location_return_staus_200_validate_data() throws IOException {

        List<LocationResponse> locations = getLocationResponseData();

        ElinkLocationResponse elinkLocationResponse = new ElinkLocationResponse();
        elinkLocationResponse.setResults(locations);

        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClientOne.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());


        ResponseEntity<String> responseEntity = eLinksServiceImpl.retrieveLocation();

        List<Location> result = locationRepository.findAllById(List.of("1","2","3"));

        assertThat(result.size()).isEqualTo(3);

        assertThat(result.get(0).getRegionId()).isEqualTo("1");
        assertThat(result.get(0).getRegionDescEn()).isEqualTo("National");
        assertThat(result.get(0).getRegionDescCy()).isBlank();

        assertThat(result.get(1).getRegionId()).isEqualTo("2");
        assertThat(result.get(1).getRegionDescEn()).isEqualTo("National England and Wales");
        assertThat(result.get(1).getRegionDescCy()).isBlank();

    }

    private List<LocationResponse> getLocationResponseData() {


        LocationResponse locationOne = new LocationResponse();
        locationOne.setId("1");
        locationOne.setName("National");
        locationOne.setCreatedAt("2022-10-03T15:28:19Z");
        locationOne.setUpdatedAt("2022-10-03T15:28:19Z");

        LocationResponse locationTwo = new LocationResponse();
        locationTwo.setId("2");
        locationTwo.setName("National England and Wales");
        locationTwo.setCreatedAt("2022-10-03T15:28:19Z");
        locationTwo.setUpdatedAt("2022-10-03T15:28:19Z");


        LocationResponse locationThree = new LocationResponse();
        locationThree.setId("3");
        locationThree.setName("Taylor House (London)");
        locationThree.setCreatedAt("2022-10-03T15:28:19Z");
        locationThree.setUpdatedAt("2022-10-03T15:28:19Z");

        List<LocationResponse> locations = new ArrayList<>();

        locations.add(locationOne);
        locations.add(locationTwo);
        locations.add(locationThree);

        return locations;

    }

}
