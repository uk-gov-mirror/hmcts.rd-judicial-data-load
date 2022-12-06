package uk.gov.hmcts.reform.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.reform.elinks.domain.Location;


public interface LocationRepository extends JpaRepository<Location, String> {
    @Modifying(flushAutomatically = true)
    @Query(
            value = "INSERT INTO dbjudicialdata.region_type (region_id,region_desc_en,region_desc_cy) "
                    + "VALUES(:region_id,:region_desc_en,:region_desc_cy) "
                    + "ON CONFLICT (region_id) DO UPDATE "
                    + "SET region_desc_en = :region_desc_en,region_desc_cy = :region_desc_cy ",
            nativeQuery = true)
    void addRegionType(@Param("region_id") String regionId,
                          @Param("region_desc_en") String regionDescEn,
                          @Param("region_desc_cy") String regionDescCy);
}
