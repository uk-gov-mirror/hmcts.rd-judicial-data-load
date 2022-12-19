package uk.gov.hmcts.reform.elinks.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.elinks.response.ElinkLocationResponse;
import uk.gov.hmcts.reform.elinks.response.LocationResponse;

import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_DATA_STORE_ERROR;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.LOCATION_DATA_LOAD_SUCCESS;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"AbbreviationAsWordInName","MemberName"})
class ELinksServiceImplTest {

    @Mock
    BaseLocationRepository baseLocationRepository;

    @Mock
    LocationRepository locationRepository;

    @Mock
    ElinksFeignClient elinksFeignClient;

    @InjectMocks
    private ELinksServiceImpl eLinksServiceImpl;

    @Test
    void elinksService_load_location_should_return_sucess_msg_with_status_200() throws JsonProcessingException {

        List<LocationResponse> locations = getLocationResponseData();


        ElinkLocationResponse elinkLocationResponse = new ElinkLocationResponse();
        elinkLocationResponse.setResults(locations);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);


        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());

        when(locationRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        ResponseEntity<String> responseEntity = eLinksServiceImpl.retrieveLocation();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getBody().toString()).contains(LOCATION_DATA_LOAD_SUCCESS);

    }


    @Test
    void elinksService_load_location_should_return_elinksException_when_DataAccessException()
            throws JsonProcessingException {

        List<LocationResponse> locations = getLocationResponseData();


        ElinkLocationResponse elinkLocationResponse = new ElinkLocationResponse();
        elinkLocationResponse.setResults(locations);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);

        DataAccessException dataAccessException = mock(DataAccessException.class);

        when(elinksFeignClient.getLocationDetails()).thenReturn(Response.builder()
                .request(mock(Request.class)).body(body, defaultCharset()).status(HttpStatus.OK.value()).build());

        when(locationRepository.saveAll(anyList())).thenThrow(dataAccessException);
        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<String> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_DATA_STORE_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_DATA_STORE_ERROR);


    }

    @Test
    void elinksService_load_location_should_return_elinksException_when_FeignException()
            throws JsonProcessingException {

        List<LocationResponse> locations = getLocationResponseData();


        ElinkLocationResponse elinkLocationResponse = new ElinkLocationResponse();
        elinkLocationResponse.setResults(locations);

        FeignException feignExceptionMock = Mockito.mock(FeignException.class);


        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(elinkLocationResponse);

        when(elinksFeignClient.getLocationDetails()).thenThrow(feignExceptionMock);


        ElinksException thrown = Assertions.assertThrows(ElinksException.class, () -> {
            ResponseEntity<String> responseEntity = eLinksServiceImpl.retrieveLocation();
        });

        assertThat(thrown.getStatus().value()).isEqualTo(HttpStatus.FORBIDDEN.value());

        assertThat(thrown.getErrorMessage()).contains(ELINKS_ACCESS_ERROR);
        assertThat(thrown.getErrorDescription()).contains(ELINKS_ACCESS_ERROR);


    }

    private List<LocationResponse> getLocationResponseData() {


        LocationResponse locationOne = new LocationResponse();
        locationOne.setId("1");
        locationOne.setName("National");
        locationOne.setCreatedAt("2022-10-03T15:28:19Z");
        locationOne.setUpdatedAt("2022-10-03T15:28:19Z");


        List<LocationResponse> locations = new ArrayList<>();

        locations.add(locationOne);

        return locations;

    }

}
