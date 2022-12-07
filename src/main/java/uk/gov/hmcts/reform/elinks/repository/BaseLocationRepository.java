package uk.gov.hmcts.reform.elinks.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
import uk.gov.hmcts.reform.elinks.domain.BaseLocation;
@SuppressWarnings("all")
public interface BaseLocationRepository extends JpaRepository<BaseLocation, String> {



}
