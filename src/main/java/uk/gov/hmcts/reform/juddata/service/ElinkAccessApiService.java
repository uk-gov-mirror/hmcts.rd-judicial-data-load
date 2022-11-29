package uk.gov.hmcts.reform.juddata.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ElinkAccessApiService {

    private static final Logger logger = LogManager.getLogger(ElinkAccessApiService.class);

    @Value("${bearerbearerAuthorizationKey}")
    private String bearerAuthorization;
    @Value("${s2ss2sServicebearerAuthorizationKey}")
    private String s2sServicebearerAuthorization;
    @Value("${regionIdurl}")
    private String regionIdurl;
    @Value("${orgServiceUrl}")
    private String orgServiceUrl;

    public List<ResponseEntity> retrieveCitiesDetails(String regionId, String orgServiceCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("bearerAuthorization", bearerAuthorization);
        headers.set("s2sServicebearerAuthorization", s2sServicebearerAuthorization);

        @SuppressWarnings("unchecked")
        HttpEntity entity = new HttpEntity(headers);

        logger.info("Location Region api call: " + LocalDateTime.now());

        logger.info("Location OrgService api call: " + LocalDateTime.now());


        RestTemplate template = new RestTemplate();
        ResponseEntity<String> regionIdResponseEntity = template.exchange(regionIdurl, HttpMethod.GET, entity,
                String.class, regionId);
        ResponseEntity<String> orgServiceResponseEntity = template.exchange(orgServiceUrl, HttpMethod.GET, entity,
                String.class, orgServiceCode);


        return Arrays.asList(regionIdResponseEntity, orgServiceResponseEntity);


    }

}
