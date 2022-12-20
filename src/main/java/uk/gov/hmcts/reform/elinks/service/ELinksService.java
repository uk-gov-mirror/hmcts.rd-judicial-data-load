package uk.gov.hmcts.reform.elinks.service;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.elinks.response.ElinkLocationWrapperResponse;

public interface ELinksService {

    ResponseEntity<Object> retrieveBaseLocation();

    ResponseEntity<ElinkLocationWrapperResponse> retrieveLocation();

}
