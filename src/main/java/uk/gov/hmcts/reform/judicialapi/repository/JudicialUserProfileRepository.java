
package uk.gov.hmcts.reform.judicialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.judicialapi.domain.JudicialUserProfile;
import java.util.List;

@Repository
public interface JudicialUserProfileRepository extends JpaRepository<JudicialUserProfile, Long> {

    @Query(value = "select jup.title, jup.known_as, jup.surname, jup.full_name,jup.email_id, jup.elinks_id, " +
            "jup.object_id from judicial_user_profile jup", nativeQuery = true)
    List<JudicialUserProfile> findAllJudicialUserProfiles();
}

