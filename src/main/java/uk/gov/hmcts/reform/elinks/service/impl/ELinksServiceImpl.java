package uk.gov.hmcts.reform.elinks.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.elinks.domain.Location;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient1;
import uk.gov.hmcts.reform.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.elinks.service.ELinksService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.LOCATION_DATA_LOAD_SUCCESS;

@Service
public class ELinksServiceImpl implements ELinksService {

    @Autowired
    BaseLocationRepository baseLocationRepository;

    @Autowired
    LocationRepository locationRepository;



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

        List<Location> locations = new ArrayList<>();
        locations = elinksFeignClient.getLocationDetails();

        locationRepository.saveAll(locations);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LOCATION_DATA_LOAD_SUCCESS);
    }


}
