package uk.gov.hmcts.reform.elinks.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;

public interface LocationRepository extends JpaRepository<BaseLocation, String> {

}
