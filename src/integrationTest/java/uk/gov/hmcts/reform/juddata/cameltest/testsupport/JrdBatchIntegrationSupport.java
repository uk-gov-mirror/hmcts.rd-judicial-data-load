package uk.gov.hmcts.reform.juddata.cameltest.testsupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.reform.juddata.cameltest.testsupport.IntegrationTestSupport.getFileAuthorisationObjectsFromCsv;
import static uk.gov.hmcts.reform.juddata.cameltest.testsupport.IntegrationTestSupport.handleNull;
import static uk.gov.hmcts.reform.juddata.cameltest.testsupport.ParentIntegrationTestSupport.file;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.assertj.core.api.Assertions;
import org.hibernate.validator.internal.util.Contracts;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.ExceptionProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.route.LoadRoutes;
import uk.gov.hmcts.reform.data.ingestion.camel.service.EmailService;
import uk.gov.hmcts.reform.data.ingestion.camel.util.DataLoadUtil;
import uk.gov.hmcts.reform.juddata.camel.binder.JudicialOfficeAuthorisation;
import uk.gov.hmcts.reform.juddata.camel.service.AuditProcessingService;

public abstract class JrdBatchIntegrationSupport {


    public static final String DB_SCHEDULER_STATUS = "scheduler_status";

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected LoadRoutes parentRoute;

    @Value("${start-route}")
    protected String startRoute;

    @Autowired
    protected ProducerTemplate producerTemplate;

    @Value("${parent-select-jrd-sql}")
    protected String userProfileSql;

    @Value("${child-select-child1-sql}")
    protected String sqlChild1;

    @Value("${child-select-child2-sql}")
    protected String sqlChild2;

    @Value("${archival-cred}")
    protected String archivalCred;

    @Value("${select-dataload-schedular-audit}")
    protected String selectDataLoadSchedulerAudit;

    @Value("${scheduler-insert-sql}")
    protected String schedulerInsertJrdSql;

    @Value("${select-dataload-scheduler-audit-failure}")
    protected String schedulerInsertJrdSqlFailure;

    @Value("${select-dataload-scheduler-audit-partial-success}")
    protected String schedulerInsertJrdSqlPartialSuccess;

    @Value("${select-dataload-scheduler-audit-success}")
    protected String schedulerInsertJrdSqlSuccess;

    @Value("${audit-enable}")
    protected Boolean auditEnable;

    @Autowired
    protected DataLoadUtil dataLoadUtil;

    @Autowired
    protected ExceptionProcessor exceptionProcessor;

    @Autowired
    protected EmailService emailService;

    @Autowired
    protected AuditProcessingService auditProcessingService;

    @Value("${base-location-select-jrd-sql}")
    protected String baseLocationSql;

    @Value("${region-select-jrd-sql}")
    protected String regionSql;

    @Value("${contract-select-jrd-sql}")
    protected String contractSql;

    @Value("${role-select-jrd-sql}")
    protected String roleSql;


    @Value("${start-leaf-route}")
    protected String startLeafRoute;

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;


    @Value("${exception-select-query}")
    protected String exceptionQuery;

    @Value("${truncate-audit}")
    protected String truncateAudit;

    public void validateDbRecordCountFor(String queryName, int expectedCount) {
        assertEquals(expectedCount, jdbcTemplate.queryForList(queryName).size());
    }

    public void validateExceptionDbRecordCount(String queryName, int expectedCount, boolean isPartialSuccessValidation) {
        List<Map<String, Object>> exceptionList = jdbcTemplate.queryForList(queryName);

        exceptionList.forEach(exception -> {
            assertNotNull(exception.get("scheduler_name"));
            assertNotNull(exception.get("scheduler_start_time"));
            assertNotNull(exception.get("error_description"));
            assertNotNull(exception.get("updated_timestamp"));
            if (isPartialSuccessValidation) {
                assertNotNull(exception.get("table_name"));
                assertNotNull(exception.get("key"));
                assertNotNull(exception.get("field_in_error"));
            }
        });
        assertEquals(expectedCount, exceptionList.size());
    }

    public void validateUserProfileFile() {
        List<Map<String, Object>> judicialUserProfileList = jdbcTemplate.queryForList(userProfileSql);
        assertEquals(judicialUserProfileList.size(), 2);
        Contracts.assertNotNull(judicialUserProfileList.get(0));
        Contracts.assertNotNull(judicialUserProfileList.get(1));
        assertEquals(judicialUserProfileList.get(0).get("elinks_id"), "1");
        assertEquals(judicialUserProfileList.get(1).get("elinks_id"), "2");
        assertEquals(judicialUserProfileList.get(0).get("email_id"), "joe.bloggs@ejudiciary.net");
        assertEquals(judicialUserProfileList.get(1).get("email_id"), "jo1e.bloggs@ejudiciary.net");
    }

    public void validateAppointmentFile() {
        List<Map<String, Object>> judicialAppointmentList = jdbcTemplate.queryForList(sqlChild1);
        assertEquals(judicialAppointmentList.size(), 2);
        Contracts.assertNotNull(judicialAppointmentList.get(0));
        Contracts.assertNotNull(judicialAppointmentList.get(1));
        Contracts.assertNotNull(judicialAppointmentList.get(0).get("judicial_office_appointment_id"));
        Contracts.assertNotNull(judicialAppointmentList.get(0).get("judicial_office_appointment_id"));
        assertEquals("1", judicialAppointmentList.get(0).get("elinks_id"));
        assertEquals("2", judicialAppointmentList.get(1).get("elinks_id"));
    }

    public void validateAuthorisationFile() {
        List<Map<String, Object>> judicialAuthorisationList = jdbcTemplate.queryForList(sqlChild2);
        List<JudicialOfficeAuthorisation> actualAuthorisations = judicialAuthorisationList.stream().map(authorisationMap -> {
            JudicialOfficeAuthorisation judicialOfficeAuthorisation = new JudicialOfficeAuthorisation();
            judicialOfficeAuthorisation.setElinksId((String)authorisationMap.get("elinks_id"));
            judicialOfficeAuthorisation.setJurisdiction((String)authorisationMap.get("jurisdiction"));
            judicialOfficeAuthorisation.setTicketId((Long)authorisationMap.get("ticket_id"));
            judicialOfficeAuthorisation.setStartDate(handleNull((Timestamp)authorisationMap.get("start_date")));
            judicialOfficeAuthorisation.setEndDate(handleNull((Timestamp)authorisationMap.get("end_date")));
            judicialOfficeAuthorisation.setCreatedDate(handleNull((Timestamp)authorisationMap.get("created_date")));
            judicialOfficeAuthorisation.setLastUpdated(handleNull((Timestamp)authorisationMap.get("last_updated")));
            judicialOfficeAuthorisation.setLowerLevel((String)authorisationMap.get("lower_level"));
            return judicialOfficeAuthorisation;
        }).collect(Collectors.toList());

        //size check
        List<JudicialOfficeAuthorisation> expectedAuthorisations = getFileAuthorisationObjectsFromCsv(file[2]);
        assertEquals(judicialAuthorisationList.size(), expectedAuthorisations.size());
        //exact field checks
        Assertions.assertThat(actualAuthorisations).usingFieldByFieldElementComparator().containsAll(expectedAuthorisations);
    }

}
