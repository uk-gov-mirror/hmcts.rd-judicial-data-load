package uk.gov.hmcts.reform.elinks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.elinks.service.ELinksService;
import uk.gov.hmcts.reform.elinks.service.IdamElasticSearchService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.LOCATION_DATA_LOAD_SUCCESS;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"AbbreviationAsWordInName","MemberName"})
public class ELinksControllerTest {

    @InjectMocks
    ELinksController eLinksController;

    @Mock
    IdamElasticSearchService idamElasticSearchService;

    @Mock
    ELinksService eLinksService;

    @Test
    void test_load_location_success() {

        ResponseEntity<ElinkLocationWrapperResponse> responseEntity;

        ElinkLocationWrapperResponse elinkLocationWrapperResponse = new ElinkLocationWrapperResponse();
        elinkLocationWrapperResponse.setMessage(LOCATION_DATA_LOAD_SUCCESS);



        responseEntity = new ResponseEntity<>(
                elinkLocationWrapperResponse,
                null,
                HttpStatus.OK
        );

        when(eLinksService.retrieveLocation()).thenReturn(responseEntity);

        ResponseEntity<ElinkLocationWrapperResponse> actual = eLinksController.loadLocation();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getBody().getMessage()).isEqualTo(LOCATION_DATA_LOAD_SUCCESS);

    }

    @Test
    void test_load_base_location_success() {

        ResponseEntity<Object> responseEntity;

        responseEntity = new ResponseEntity<>(
                BASE_LOCATION_DATA_LOAD_SUCCESS,
                null,
                HttpStatus.OK
        );

        when(eLinksService.retrieveBaseLocation()).thenReturn(responseEntity);

        ResponseEntity<Object> actual = eLinksController.loadBaseLocationType();
        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getBody().toString()).isEqualTo(BASE_LOCATION_DATA_LOAD_SUCCESS);

    }


}
