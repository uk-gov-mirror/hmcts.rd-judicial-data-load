package uk.gov.hmcts.reform.elinks.service.impl;

import feign.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.elinks.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.elinks.domain.Location;
import uk.gov.hmcts.reform.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient;
import uk.gov.hmcts.reform.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.elinks.response.ElinkLocationResponse;
import uk.gov.hmcts.reform.elinks.response.LocationResponse;
import uk.gov.hmcts.reform.elinks.service.ELinksService;
import uk.gov.hmcts.reform.juddata.camel.util.JsonFeignResponseUtil;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.LOCATION_DATA_LOAD_SUCCESS;

@Service
public class ELinksServiceImpl implements ELinksService {

    @Autowired
    BaseLocationRepository baseLocationRepository;

    @Autowired
    LocationRepository locationRepository;



    @Autowired
    ElinksFeignClient elinksFeignClient;

    @Override
    public ResponseEntity<Object> retrieveBaseLocation() {


        List<BaseLocation> baseLocations = new ArrayList<>();
        baseLocations = elinksFeignClient.retrieveBaseLocations();




        baseLocationRepository.saveAll(baseLocations);


        return null;
    }

    @Override
    public ResponseEntity<Object> retrieveLocation() {

        //Response lrdOrgInfoServiceResponse =
                //locationReferenceDataFeignClient.getLocationRefServiceMapping(ccdServiceNames);

        Response locationsResponse = elinksFeignClient.getLocationDetails();

        HttpStatus httpStatus = HttpStatus.valueOf(locationsResponse.status());
        if (httpStatus.is2xxSuccessful()) {
//            ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toResponseEntityWithListBody(locationsResponse,
//                    Location.class);
            ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toELinksResponseEntity(locationsResponse,
                   ElinkLocationResponse.class);


            ElinkLocationResponse elinkLocationResponse = (ElinkLocationResponse) responseEntity.getBody();

                    List<LocationResponse> locationResponseList = elinkLocationResponse.getResults();

            List<Location> locations = locationResponseList.stream()
                    .map(locationRes -> new Location(locationRes.getId(), locationRes.getName(), StringUtils.EMPTY))
                    .toList();

                 locationRepository.saveAll(locations);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LOCATION_DATA_LOAD_SUCCESS);

        }

        ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toELinksResponseEntity(locationsResponse,
                ErrorResponse.class);
        Object responseBody = responseEntity.getBody();
        if (nonNull(responseBody) && responseBody instanceof ErrorResponse errorResponse) {

            throw new ElinksException(httpStatus, errorResponse.getErrorMessage(),
                    errorResponse.getErrorDescription());
        } else {
            throw new ElinksException(httpStatus, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }


    }


}
