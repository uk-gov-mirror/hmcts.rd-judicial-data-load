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
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.elinks.response.BaseLocationResponse;
import uk.gov.hmcts.reform.elinks.response.ElinkBaseLocationResponse;
import uk.gov.hmcts.reform.elinks.response.ElinkBaseLocationWrapperResponse;
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
public class ELinksServiceImplBaseLocationTest {


    @Autowired
    BaseLocationRepository baseLocationRepository;

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
        assertThat(baseLocationRepository).isNotNull();
        assertThat(eLinksServiceImpl).isNotNull();
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

    @Test
    void test_elinksService_load_baseLocation_return_staus_200_validate_data() throws IOException {

        List<BaseLocationResponse> baseLocations = getBaseLocationResponseData();


        ElinkBaseLocationResponse elinkBaseLocationResponse = new ElinkBaseLocationResponse();
        elinkBaseLocationResponse.setResults(baseLocations);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkBaseLocationResponse);


        when(elinksFeignClientOne.getBaseLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());


        ResponseEntity<ElinkBaseLocationWrapperResponse> responseEntity = eLinksServiceImpl.retrieveBaseLocation();

        List<BaseLocation> result = baseLocationRepository.findAllById(List.of("1","2"));

        assertThat(result.size()).isEqualTo(2);

        BaseLocation baseLocationOne = getBaseLocationEntityList().get(0);
        BaseLocation baseLocationTwo = getBaseLocationEntityList().get(1);


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
