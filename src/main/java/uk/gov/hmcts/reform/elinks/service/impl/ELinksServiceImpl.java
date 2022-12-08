package uk.gov.hmcts.reform.elinks.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient1;
import uk.gov.hmcts.reform.elinks.service.ELinksService;

@Service
public class ELinksServiceImpl implements ELinksService {

    @Autowired
    ElinksFeignClient1 elinksFeignClient;

    @Override
    public ResponseEntity<Object> retrieveBaseLocation() {
        return null;
    }

    @Override
    public ResponseEntity<Object> retrieveLocation() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(elinksFeignClient.getLocationDetails());
    }


}
