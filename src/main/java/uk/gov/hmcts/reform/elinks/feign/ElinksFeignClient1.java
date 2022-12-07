package uk.gov.hmcts.reform.elinks.feign;

import feign.Headers;
import feign.RequestLine;
import feign.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.hmcts.reform.elinks.configuration.ElinksFeignInterceptorConfiguration;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.elinks.domain.Location;

import java.util.List;

@FeignClient(name = "ElinksClient", url = "${eLinksUrl}", configuration = ElinksFeignInterceptorConfiguration.class)
public interface ElinksFeignClient1 {

    @GetMapping(value = "/base_location")
    @RequestLine("GET /base_location")
    @Headers({"Authorization: {authorization}", "ServiceAuthorization: {serviceAuthorization}",
            "Content-Type: application/json"})
    List<BaseLocation> retrieveBaseLocations();

    //URL not tested , code is for developers reference
    @GetMapping(value = "/location")
    @RequestLine("GET /location")
    @Headers({"Authorization: {authorization}",
            "Content-Type: application/json"})
    List<Location> getLocationDetails();


}
