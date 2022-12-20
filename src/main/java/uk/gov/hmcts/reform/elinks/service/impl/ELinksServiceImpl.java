package uk.gov.hmcts.reform.elinks.service.impl;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import uk.gov.hmcts.reform.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.elinks.response.LocationResponse;
import uk.gov.hmcts.reform.elinks.service.ELinksService;
import uk.gov.hmcts.reform.juddata.camel.util.JsonFeignResponseUtil;

import java.util.List;

import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_DATA_STORE_ERROR;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.LOCATION_DATA_LOAD_SUCCESS;

@Service
@Slf4j
public class ELinksServiceImpl implements ELinksService {

    @Autowired
    BaseLocationRepository baseLocationRepository;

    @Autowired
    LocationRepository locationRepository;



    @Autowired
    ElinksFeignClient elinksFeignClient;

    @Override
    public ResponseEntity<Object> retrieveBaseLocation() {


        List<BaseLocation> baseLocations = elinksFeignClient.retrieveBaseLocations();

        baseLocationRepository.saveAll(baseLocations);

        return null;
    }

    @Override
    public ResponseEntity<ElinkLocationWrapperResponse> retrieveLocation() {

        log.info("Get location details ELinksService.retrieveLocation ");

        Response locationsResponse = null;
        HttpStatus httpStatus = null;
        ResponseEntity<ElinkLocationWrapperResponse> result = null;
        try {

            locationsResponse = elinksFeignClient.getLocationDetails();

            httpStatus = HttpStatus.valueOf(locationsResponse.status());

            log.info("Get location details response status ELinksService.retrieveLocation" + httpStatus.value());
            if (httpStatus.is2xxSuccessful()) {
                ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toELinksResponseEntity(locationsResponse,
                        ElinkLocationResponse.class);


                ElinkLocationResponse elinkLocationResponse = (ElinkLocationResponse) responseEntity.getBody();

                List<LocationResponse> locationResponseList = elinkLocationResponse.getResults();

                List<Location> locations = locationResponseList.stream()
                        .map(locationRes -> new Location(locationRes.getId(), locationRes.getName(), StringUtils.EMPTY))
                        .toList();
                result =  loadLocationData(locations);

            } else {
                handleELinksErrorResponse(locationsResponse, httpStatus);
            }


        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
        return result;
    }

    private void handleELinksErrorResponse(Response locationsResponse, HttpStatus httpStatus) {
        ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toELinksResponseEntity(locationsResponse,
                ErrorResponse.class);
        Object responseBody = responseEntity.getBody();


        if (HttpStatus.BAD_REQUEST.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_BAD_REQUEST,
                    ELINKS_ERROR_RESPONSE_BAD_REQUEST);
        } else if (HttpStatus.UNAUTHORIZED.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_UNAUTHORIZED,
                    ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
        } else if (HttpStatus.FORBIDDEN.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_FORBIDDEN,
                    ELINKS_ERROR_RESPONSE_FORBIDDEN);
        } else if (HttpStatus.NOT_FOUND.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_NOT_FOUND,
                    ELINKS_ERROR_RESPONSE_NOT_FOUND);
        } else if (HttpStatus.TOO_MANY_REQUESTS.value() == httpStatus.value()) {

            throw new ElinksException(httpStatus, ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS,
                    ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
        } else {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }
    }

    private ResponseEntity<ElinkLocationWrapperResponse> loadLocationData(List<Location> locations) {
        ResponseEntity<ElinkLocationWrapperResponse> result = null;
        try {

            locationRepository.saveAll(locations);

            ElinkLocationWrapperResponse elinkLocationWrapperResponse = new ElinkLocationWrapperResponse();
            elinkLocationWrapperResponse.setMessage(LOCATION_DATA_LOAD_SUCCESS);


            result =  ResponseEntity
                    .status(HttpStatus.OK)
                    .body(elinkLocationWrapperResponse);
        } catch (DataAccessException dae) {

            throw new ElinksException(HttpStatus.INTERNAL_SERVER_ERROR, ELINKS_DATA_STORE_ERROR,
                    ELINKS_DATA_STORE_ERROR);
        }

        return result;
    }


}
