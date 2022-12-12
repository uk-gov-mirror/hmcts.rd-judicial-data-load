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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.hmcts.reform.data.ingestion.camel.service.EmailServiceImpl;
import uk.gov.hmcts.reform.elinks.configuration.ElinkEmailConfiguration;
import uk.gov.hmcts.reform.elinks.response.SchedulerJobStatusResponse;
import uk.gov.hmcts.reform.elinks.servicebus.ElinkTopicPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.reform.elinks.util.JobStatus.*;
import static uk.gov.hmcts.reform.elinks.util.RefDataConstants.ROW_MAPPER;
import static uk.gov.hmcts.reform.elinks.util.SqlContants.SELECT_JOB_STATUS_SQL;
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

    @Mock
    SchedulerJobStatusResponse schedulerJobStatusResponse;

    List<String> sidamIds = new ArrayList<>();

    EmailServiceImpl emailService = mock(EmailServiceImpl.class);

    @BeforeEach
    public void beforeTest() throws Exception {

        sidamIds.add(UUID.randomUUID().toString());
        publishSidamIdService.logComponentName = "RD_Elink_Judicial_Ref_Data";
    }
    @SneakyThrows
    @Test
    @DisplayName("Positive Scenario: Pusblish message to ASb when job status is in progress")
    void should_publish_when_job_status_is_inprogress() {

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
                .thenReturn(Pair.of("2", IN_PROGRESS.getStatus()));

        publishSidamIdService.publishSidamIdToAsb();
        verify(elinkTopicPublisher, times(1)).sendMessage(any(), anyString());
    }

    @SneakyThrows
    @Test
    @DisplayName("Should retry when job status is_failed")
    void should_retry_when_job_status_is_failed() {
        when(jdbcTemplate.query(GET_DISTINCT_SIDAM_ID, ROW_MAPPER)).thenReturn(sidamIds);
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
                .thenReturn(Pair.of("1", FAILED.getStatus()));

        publishSidamIdService.publishSidamIdToAsb();
        verify(elinkTopicPublisher, times(1)).sendMessage(any(), anyString());

    }


    @SneakyThrows
    @Test
    @DisplayName("When no sidam id's is available then just update the status in db")
    void update_succes_as_job_status_when_no_sidam_ids_available() {
        List<String> sidamIds = new ArrayList<>();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
                .thenReturn(Pair.of("2", SUCCESS.getStatus()));

        when(jdbcTemplate.query(GET_DISTINCT_SIDAM_ID, ROW_MAPPER)).thenReturn(sidamIds);
        publishSidamIdService.publishSidamIdToAsb();
        verify(jdbcTemplate).update(anyString(), any(), anyInt());

    }

    @SneakyThrows
    @Test
    @DisplayName("Negative Scenario: Should throw exception when job status is as FILE_LOAD_FAILED")
    void should_throw_exception_for_status_as_file_load_failed() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
                .thenReturn(Pair.of("2", FILE_LOAD_FAILED.getStatus()));
        when(publishSidamIdService.publishSidamIdToAsb()).thenThrow(new RuntimeException("any exception"));

        assertThrows(Exception.class, () -> publishSidamIdService.publishSidamIdToAsb());
        verify(jdbcTemplate).update(anyString(), any(), anyInt());
    }


    @SneakyThrows
    @Test
    @DisplayName("Negative Scenario: should throw exception for any issue while publishing message")
    void should_throw_exception_when_any_issue_comes() {
        when(publishSidamIdService.publishSidamIdToAsb()).thenThrow(new RuntimeException("any exception"));

        Exception msg = assertThrows(Exception.class, () -> publishSidamIdService.publishSidamIdToAsb());
        assertEquals("any exception", msg.getMessage());

        verify(jdbcTemplate).update(anyString(), any(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("Negative Scenario: should throw exception for any issue during get job status")
    void test_when_get_job_details_runs_into_an_exception() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
                .thenThrow(new EmptyResultDataAccessException(1));
        publishSidamIdService.publishSidamIdToAsb();

        verify(elinkTopicPublisher, times(0)).sendMessage(any(), anyString());
    }
    @SneakyThrows
    @Test
    @DisplayName("Negative Scenario: should throw exception when emailis not enabled")
    void should_throw_exception_when_email_is_not_enabled() throws Exception{
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
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

        Exception msg =assertThrows(Exception.class,
                () -> publishSidamIdService.publishSidamIdToAsb());
        System.out.println("Mesage: "+msg.getMessage());
        assertTrue(msg.getMessage().contentEquals("ASB Failure Root cause - {}"));
//        verify(elinkTopicPublisher).sendMessage(any(), anyString());
//        verify(jdbcTemplate).update(anyString(), any(), anyInt());
//        verify(emailService).sendEmail(any(Email.class));
    }

}