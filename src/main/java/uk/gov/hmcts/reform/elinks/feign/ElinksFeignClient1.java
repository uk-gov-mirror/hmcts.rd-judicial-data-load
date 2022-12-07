package uk.gov.hmcts.reform.elinks.feign;

import feign.Headers;
import feign.RequestLine;
import feign.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.hmcts.reform.elinks.configuration.ElinksFeignInterceptorConfiguration;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;

import java.util.List;

@FeignClient(name = "ElinksClient", url = "${eLinksUrl}", configuration = ElinksFeignInterceptorConfiguration.class, fallback = BaseLocationFallBack.class)
public interface ElinksFeignClient1 {

    @GetMapping(value = "/base_location")
    @RequestLine("GET /base_location")
    @Headers({"Authorization: {authorization}", "ServiceAuthorization: {serviceAuthorization}",
            "Content-Type: application/json"})
    List<BaseLocation> retrieveBaseLocations();


}
