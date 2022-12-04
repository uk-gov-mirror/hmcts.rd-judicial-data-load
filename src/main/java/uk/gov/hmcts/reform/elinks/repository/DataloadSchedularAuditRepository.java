package uk.gov.hmcts.reform.elinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.elinks.domain.DataloadSchedularAudit;

import java.time.LocalDateTime;

public interface DataloadSchedularAuditRepository extends JpaRepository<DataloadSchedularAudit, String> {

    @Query(value = "dbjudicialdata.SELECT max(scheduler_end_time) FROM dataload_schedular_audit "
            + "WHERE scheduler_end_time < (dbjudicialdata.SELECT MAX(scheduler_end_time) FROM dataload_schedular_audit "
            + "WHERE file_name = 'Personal' AND status IN ('Success')) "
            + "AND file_name = 'Personal' AND status IN ('Success')")
    LocalDateTime findByScheduleEndTime();
}
