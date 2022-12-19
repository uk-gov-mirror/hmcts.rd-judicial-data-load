package uk.gov.hmcts.reform.elinks.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.hmcts.reform.elinks.response.LocationResponse;
import uk.gov.hmcts.reform.elinks.service.ELinksService;
import uk.gov.hmcts.reform.juddata.camel.util.JsonFeignResponseUtil;

import java.util.List;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_ACCESS_ERROR;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ELINKS_DATA_STORE_ERROR;
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
    public ResponseEntity<String> retrieveLocation() {

        log.info("Get location details ELinksService.retrieveLocation ");

        Response locationsResponse = null;
        HttpStatus httpStatus = null;
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
                return loadLocationData(locations);

            }

            ResponseEntity<Object> responseEntity = JsonFeignResponseUtil.toELinksResponseEntity(locationsResponse,
                    ErrorResponse.class);
            Object responseBody = responseEntity.getBody();
            if (nonNull(responseBody) && responseBody instanceof ErrorResponse errorResponse) {

                throw new ElinksException(httpStatus, ELINKS_ACCESS_ERROR,
                        ELINKS_ACCESS_ERROR);
            } else {
                throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
            }
        } catch (FeignException ex) {
            throw new ElinksException(HttpStatus.FORBIDDEN, ELINKS_ACCESS_ERROR, ELINKS_ACCESS_ERROR);
        }

    }

    private ResponseEntity<String> loadLocationData(List<Location> locations) {
        ResponseEntity<String> result = null;
        try {

            locationRepository.saveAll(locations);

            ObjectMapper mapper = new ObjectMapper();


            result =  ResponseEntity
                    .status(HttpStatus.OK)
                    .body(mapper.writeValueAsString(LOCATION_DATA_LOAD_SUCCESS));
        } catch (JsonProcessingException jpe) {
            log.error(jpe.getMessage());
        } catch (DataAccessException dae) {

            throw new ElinksException(HttpStatus.INTERNAL_SERVER_ERROR, ELINKS_DATA_STORE_ERROR,
                    ELINKS_DATA_STORE_ERROR);
        }

        return result;
    }


}
