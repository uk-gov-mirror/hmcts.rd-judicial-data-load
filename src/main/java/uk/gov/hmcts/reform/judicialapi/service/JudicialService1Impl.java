
package uk.gov.hmcts.reform.judicialapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import uk.gov.hmcts.reform.judicialapi.repository.JudicialUserProfileRepository;
import java.util.List;

@Slf4j
@Service
public class JudicialService1Impl {

    @Autowired
    JudicialUserProfileRepository judicialUserProfileRepository;

    @Cacheable(value = "autocomplete")
    public List<JudicialUserProfile> findAllJudicialUserProfiles() {
        log.info("::::: findAllJudicialUserProfiles called :::::");
        return judicialUserProfileRepository.findAllJudicialUserProfiles();
    }
}

