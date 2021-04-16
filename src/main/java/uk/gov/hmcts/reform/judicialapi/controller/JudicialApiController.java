package uk.gov.hmcts.reform.judicialapi.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.service.JudicialServiceImpl;
import java.util.List;

@RequestMapping(path = "judicialdata/profiles")
@RestController
@Slf4j
public class JudicialApiController {

    @Autowired
    JudicialServiceImpl judicialService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JudicialUserProfile>> retrieveOrganisationUsingOrgIdentifier(@RequestParam(value = "searchChars") String searchChars) {
        List<JudicialUserProfile> profiles = judicialService.findAllJudicialUserProfiles(searchChars);
        return ResponseEntity.ok().body(profiles);
    }
}

