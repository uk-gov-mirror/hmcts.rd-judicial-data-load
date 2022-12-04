package uk.gov.hmcts.reform.elinks.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.elinks.exception.JudicialDataLoadException;
import uk.gov.hmcts.reform.elinks.feign.IdamFeignClient;
import uk.gov.hmcts.reform.elinks.repository.DataloadSchedularAuditRepository;
import uk.gov.hmcts.reform.elinks.response.IdamOpenIdTokenResponse;
import uk.gov.hmcts.reform.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.elinks.service.IdamTokenService;
import uk.gov.hmcts.reform.elinks.util.JsonFeignResponseUtility;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class IdamElasticSearchServiceImpl implements IdamTokenService {

    @Autowired
    IdamFeignClient idamFeignClient;

    @Autowired
    IdamTokenConfigProperties props;

    @Value("${logging-component-name}")
    String loggingComponentName;

    @Value("${elastic.search.query}")
    String searchQuery;

    @Value("${elastic.search.recordsPerPage}")
    int recordsPerPage;

    @Autowired
    DataloadSchedularAuditRepository dataloadSchedularAuditRepository;

    @Override
    public String getBearerToken() throws JudicialDataLoadException {

        byte[] base64UserDetails = Base64.getDecoder().decode(props.getAuthorization());
        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", "password");
        String[] userDetails = new String(base64UserDetails).split(":");
        formParams.put("username", userDetails[0].trim());
        formParams.put("password", userDetails[1].trim());
        formParams.put("client_id", props.getClientId());
        byte[] base64ClientAuth = Base64.getDecoder().decode(props.getClientAuthorization());
        String[] clientAuth = new String(base64ClientAuth).split(":");
        formParams.put("client_secret", clientAuth[1]);
        formParams.put("redirect_uri", props.getRedirectUri());
        formParams.put("scope", "openid profile roles manage-user create-user search-user");


        IdamOpenIdTokenResponse openIdTokenResponse = idamFeignClient.getOpenIdToken(formParams);

        if (openIdTokenResponse == null) {
            throw new JudicialDataLoadException("Idam Service Failed while bearer token generate");
        }
        return openIdTokenResponse.getAccessToken();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<IdamResponse> getSyncFeed() throws JudicialDataLoadException {
        Map<String, String> formParams = new HashMap<>();
        searchQuery = elasticSearchQuery();
        formParams.put("query",searchQuery);
        formParams.put("size",String.valueOf(recordsPerPage));
        log.info("{}:: search elk query {}", loggingComponentName, searchQuery);
        Set<IdamResponse> judicialUsers = new HashSet<>();
        int totalCount = 0;
        int counter = 0;

        do {
            formParams.put("page", String.valueOf(counter));
            String bearerToken = "Bearer ".concat(getBearerToken());
            Response response = idamFeignClient.getUserFeed(bearerToken, formParams);
            logIdamResponse(response);

            ResponseEntity<Object> responseEntity = JsonFeignResponseUtility.toResponseEntity(response,
                new TypeReference<Set<IdamResponse>>() {
                });

            if (response.status() == 200) {

                Set<IdamResponse> users = (Set<IdamResponse>) responseEntity.getBody();
                judicialUsers.addAll(users);

                try {
                    List<String> headerCount = responseEntity.getHeaders().get("X-Total-Count");
                    if (headerCount != null && !headerCount.isEmpty()
                        && !headerCount.get(0).isEmpty()) {

                        totalCount = Integer.parseInt(headerCount.get(0));
                        log.info("{}:: Header Records count from Idam :: " + totalCount, loggingComponentName);
                    }

                } catch (Exception ex) {
                    //There is No header.
                    log.error("{}:: X-Total-Count header not return Idam Search Service::{}", loggingComponentName, ex);
                    throw new JudicialDataLoadException("Idam search query failure");
                }
            } else {
                log.error("{}:: Idam Search Service Failed :: ", loggingComponentName);
                throw new JudicialDataLoadException("Idam search query failure with response status "
                    + response.status());
            }
            counter++;

        } while (totalCount > 0 && recordsPerPage * counter < totalCount);
        return judicialUsers;
    }

    private void logIdamResponse(Response response) {
        log.info("Logging Response from IDAM");
        if (response != null) {
            log.info("{}:: Response code from idamClient.getUserFeed {}", loggingComponentName, response.status());
            if (response.status() != 200 && response.body() != null) {
                log.info("{}:: Response body from Idam Client ::{}", loggingComponentName, response.status());
            }
        }
    }

    private String elasticSearchQuery() {

        LocalDateTime maxSchedulerEndTime = dataloadSchedularAuditRepository.findByScheduleEndTime();

        return maxSchedulerEndTime == null ? String.format(searchQuery,72) : String.format(searchQuery,
                Math.addExact(ChronoUnit.HOURS.between(maxSchedulerEndTime, LocalDateTime.now()), 1));
    }
}
