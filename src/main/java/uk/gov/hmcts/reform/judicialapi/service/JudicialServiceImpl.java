
package uk.gov.hmcts.reform.judicialapi.service;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Setter
public class JudicialServiceImpl {

    @Autowired
    JudicialService1Impl judicialService1;

    public List<JudicialUserProfile> findAllJudicialUserProfiles(String searchString) {
        List<JudicialUserProfile> profiles = judicialService1.findAllJudicialUserProfiles();
        return profiles.stream().filter( profile ->
                profile.getTitle().contains(searchString) ||
                        profile.getFull_name().contains(searchString) ||
                        profile.getKnown_as().contains(searchString) ||
                        profile.getEmail_id().contains(searchString))
                .collect(Collectors.toList());
    }
}

