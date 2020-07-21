package uk.gov.hmcts.reform.juddata.camel.listener;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class JobResultListenerTest {

    @MockBean
    JobResultListener jobResultListener;

    @MockBean
    JobExecution jobExecution;

    @Test
    public void beforeJobTest() {
        jobResultListener.beforeJob(jobExecution);
        verify(jobResultListener, times(1)).beforeJob(jobExecution);
    }

    @Test
    public void afterJobTest() {
        jobResultListener.afterJob(jobExecution);
        verify(jobResultListener, times(1)).afterJob(jobExecution);
    }
}
