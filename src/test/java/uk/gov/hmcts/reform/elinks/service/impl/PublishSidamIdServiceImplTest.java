package uk.gov.hmcts.reform.elinks.service.impl;

import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.hmcts.reform.data.ingestion.camel.service.EmailServiceImpl;
import uk.gov.hmcts.reform.elinks.configuration.ElinkEmailConfiguration;
import uk.gov.hmcts.reform.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.elinks.servicebus.ElinkTopicPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.elinks.util.JobStatus.FAILED;
import static uk.gov.hmcts.reform.elinks.util.JobStatus.IN_PROGRESS;
import static uk.gov.hmcts.reform.elinks.util.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ROW_MAPPER;
import static uk.gov.hmcts.reform.elinks.util.SqlContants.GET_DISTINCT_SIDAM_ID;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PublishSidamIdServiceImplTest {

    @InjectMocks
    PublishSidamIdServiceImpl publishSidamIdService;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    ElinkTopicPublisher elinkTopicPublisher;

    EmailServiceImpl emailService = mock(EmailServiceImpl.class);

    List<String> sidamIds = new ArrayList<>();

    @BeforeEach
    public void beforeTest() throws Exception {

        sidamIds.add("edc4190e-8e31-47d5-af56-cb7784bcd3a9");
        publishSidamIdService.logComponentName = "RD_Elink_Judicial_Ref_Data";
    }

    @SneakyThrows
    @Test
    @DisplayName("Positive Scenario: Pusblish message to ASb when job status is in progress")
    void should_publish_when_job_status_is_inprogress() {

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
                .thenReturn(Pair.of("2", IN_PROGRESS.getStatus()));

        SchedulerJobStatusResponse res = publishSidamIdService.publishSidamIdToAsb();

        assertEquals("2",res.getId());
        assertEquals("IN_PROGRESS", res.getJobStatus());
        assertEquals(HttpStatus.OK.value(),res.getStatusCode());

    }

    @SneakyThrows
    @Test
    @DisplayName("Should retry when job status is_failed")
    void should_retry_when_job_status_is_failed() {

        when(jdbcTemplate.query(GET_DISTINCT_SIDAM_ID, ROW_MAPPER)).thenReturn(sidamIds);
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
                .thenReturn(Pair.of("1", FAILED.getStatus()));

        SchedulerJobStatusResponse res = publishSidamIdService.publishSidamIdToAsb();

        assertEquals("FAILED",res.getJobStatus());
        assertEquals("1",res.getId());
        assertEquals(HttpStatus.OK.value(),res.getStatusCode());
        assertEquals("edc4190e-8e31-47d5-af56-cb7784bcd3a9",res.getSidamIds().get(0));

    }


    @SneakyThrows
    @Test
    @DisplayName("When no sidam id's is available then just update the status in db")
    void update_succes_as_job_status_when_no_sidam_ids_available() {
        List<String> sidamIds = new ArrayList<>();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
                .thenReturn(Pair.of("2", SUCCESS.getStatus()));
        when(jdbcTemplate.query(GET_DISTINCT_SIDAM_ID, ROW_MAPPER)).thenReturn(sidamIds);

        SchedulerJobStatusResponse res = publishSidamIdService.publishSidamIdToAsb();


        assertEquals("SUCCESS",res.getJobStatus());
        assertEquals(new ArrayList<>(),res.getSidamIds());
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        verify(jdbcTemplate).update(anyString(), any(), anyInt());

    }


    @SneakyThrows
    @Test
    @DisplayName("Negative Scenario: should throw exception for any issue while publishing message")
    void should_throw_exception_when_any_issue_comes() {
        when(publishSidamIdService.publishSidamIdToAsb()).thenThrow(new RuntimeException("any exception"));

        Exception msg = assertThrows(RuntimeException.class, () -> publishSidamIdService.publishSidamIdToAsb());
        assertEquals("any exception", msg.getMessage());

    }

    @SneakyThrows
    @Test
    @DisplayName("Negative Scenario: should throw exception for any issue during get job status")
    void test_when_get_job_details_runs_into_an_exception() {

        when(publishSidamIdService.publishSidamIdToAsb()).thenThrow(new EmptyResultDataAccessException(1));

        Exception msg = assertThrows(EmptyResultDataAccessException.class, () ->
                publishSidamIdService.publishSidamIdToAsb());
        assertEquals("Incorrect result size: expected 1, actual 0", msg.getMessage());
    }


    @SneakyThrows
    @Test
    @DisplayName("Negative Scenario: should throw exception when email is not enabled")
    void should_throw_exception_when_email_is_not_enabled() {

        when(publishSidamIdService.publishSidamIdToAsb())
                .thenThrow(new RuntimeException("ASB Failure Root cause - {}"));
        ElinkEmailConfiguration.MailTypeConfig mailTypeConfig = new ElinkEmailConfiguration.MailTypeConfig();
        mailTypeConfig.setEnabled(false);
        mailTypeConfig.setSubject("%s :: Publishing of JRD messages to ASB failed");
        mailTypeConfig.setBody("Publishing of JRD messages to ASB failed for Job Id %s");
        mailTypeConfig.setFrom("test@test.com");
        mailTypeConfig.setTo(List.of("test@test.com"));
        ElinkEmailConfiguration emailConfiguration = new ElinkEmailConfiguration();
        emailConfiguration.setMailTypes(Map.of("asb", mailTypeConfig));
        publishSidamIdService.emailConfiguration = emailConfiguration;

        Exception msg = assertThrows(Exception.class, () -> publishSidamIdService.publishSidamIdToAsb());
        assertTrue(msg.getMessage().contentEquals("ASB Failure Root cause - {}"));

    }

}