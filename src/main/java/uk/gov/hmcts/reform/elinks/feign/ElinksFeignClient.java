package uk.gov.hmcts.reform.elinks.feign;


import feign.Headers;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.hmcts.reform.elinks.configuration.ElinksFeignInterceptorConfiguration;
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;

import java.util.List;

@FeignClient(name = "ElinksFeignClient", url = "${elinksUrl}",
        configuration = ElinksFeignInterceptorConfiguration.class)
public interface ElinksFeignClient {

    @GetMapping(value = "/reference_data/base_location")
    @RequestLine("GET /reference_data/base_location")
    @Headers({"Authorization: {authorization}", "ServiceAuthorization: {serviceAuthorization}",
            "Content-Type: application/json"})
    List<BaseLocation> retrieveBaseLocations();

    @GetMapping(value = "/reference_data/location")
    @RequestLine("GET /reference_data/location")
    @Headers({"Authorization: {authorization}",
            "Content-Type: application/json"})
    Response getLocationDetails();


}
