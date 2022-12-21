package uk.gov.hmcts.reform.juddata.elinks.controller;

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
import uk.gov.hmcts.reform.elinks.ELinksController;
import uk.gov.hmcts.reform.elinks.configuration.ElinksFeignInterceptorConfiguration;
import uk.gov.hmcts.reform.elinks.configuration.FeignHeaderConfig;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.elinks.feign.IdamFeignClient;
import uk.gov.hmcts.reform.elinks.response.BaseLocationResponse;
import uk.gov.hmcts.reform.elinks.response.ElinkBaseLocationResponse;
import uk.gov.hmcts.reform.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.elinks.response.ElinkLocationResponse;
import uk.gov.hmcts.reform.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.elinks.response.LocationResponse;
import uk.gov.hmcts.reform.elinks.service.IdamElasticSearchService;
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
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
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
        ELinksControllerTestConfig.class,
        ELinksController.class}, initializers = ConfigDataApplicationContextInitializer.class)


@EnableAutoConfiguration
@EntityScan("uk.gov.hmcts.reform.elinks.domain")
@EnableJpaRepositories("uk.gov.hmcts.reform.elinks.repository")

@EnableFeignClients(clients = {
        ElinksFeignClient.class,
        IdamClient.class,
        IdamApi.class,
        IdamFeignClient.class})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SuppressWarnings({"AbbreviationAsWordInName","MemberName"})
public class ELinksControllerITTest {

    @Autowired
    ELinksController eLinksController;

    @Mock
    ElinksFeignClient elinksFeignClientOne;

    @Autowired
    ELinksServiceImpl eLinksServiceImpl;

    @Mock
    IdamElasticSearchService idamElasticSearchService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eLinksServiceImpl, "elinksFeignClient",
                elinksFeignClientOne
        );
    }

    @Test
    void test_elinksController_loaded() {
        assertThat(eLinksController).isNotNull();
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


        ResponseEntity<ElinkLocationWrapperResponse> responseEntity = eLinksController.loadLocation();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getBody().getMessage()).isEqualTo(LOCATION_DATA_LOAD_SUCCESS);
    }

    @Test
    void test_elinksService_load_baseLocation_return_staus_200() throws IOException {

        List<BaseLocationResponse> baseLocations = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkBaseLocationResponse = new ElinkBaseLocationResponse();
        elinkBaseLocationResponse.setResults(baseLocations);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkBaseLocationResponse);


        when(elinksFeignClientOne.getBaseLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());


        ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveBaseLocation();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getBody().getMessage()).contains(BASE_LOCATION_DATA_LOAD_SUCCESS);
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

    private List<BaseLocationResponse> getBaseLocationResponseData() {


        BaseLocationResponse baseLocationOne = new BaseLocationResponse();
        baseLocationOne.setId("1");
        baseLocationOne.setName("National");
        baseLocationOne.setCourtType("Old Gwynedd");
        baseLocationOne.setCircuit("Gwynedd");
        baseLocationOne.setAreaOfExpertise("LJA");
        baseLocationOne.setEndDate("2005-12-15");

        baseLocationOne.setCreatedAt("2022-10-03T15:28:20Z");
        baseLocationOne.setUpdatedAt("2022-10-03T15:28:20Z");

        BaseLocationResponse baseLocationTwo = new BaseLocationResponse();
        baseLocationTwo.setId("2");
        baseLocationTwo.setName("Aldridge and Brownhills");
        baseLocationTwo.setCourtType("Nottinghamshire");
        baseLocationTwo.setCircuit("Nottinghamshire");
        baseLocationTwo.setAreaOfExpertise("LJA");
        baseLocationTwo.setEndDate("2008-09-11");

        baseLocationTwo.setCreatedAt("2022-10-03T15:28:20Z");
        baseLocationTwo.setUpdatedAt("2022-10-03T15:28:20Z");


        List<BaseLocationResponse> baseLocations = new ArrayList<>();

        baseLocations.add(baseLocationOne);
        baseLocations.add(baseLocationTwo);

        return baseLocations;

    }
}
