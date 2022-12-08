package uk.gov.hmcts.reform.elinks.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;
//import uk.gov.hmcts.reform.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.elinks.feign.BaseLocationFallBack;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient1;
import uk.gov.hmcts.reform.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient1;
import uk.gov.hmcts.reform.elinks.service.ELinksService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ELinksServiceImpl implements ELinksService {

    @Autowired
    BaseLocationRepository baseLocationRepository;

    @Autowired
    BaseLocationFallBack elinksFeignClient;

    @Autowired
    ElinksFeignClient1 elinksFeignClient;

    @Override
    public ResponseEntity<Object> retrieveBaseLocation() {


        List<BaseLocation> baseLocations = new ArrayList<>();
        baseLocations = elinksFeignClient.retrieveBaseLocations();




        baseLocationRepository.saveAll(baseLocations);


        return null;
    }

    @Override
    public ResponseEntity<Object> retrieveLocation() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(elinksFeignClient.getLocationDetails());
    }


}
