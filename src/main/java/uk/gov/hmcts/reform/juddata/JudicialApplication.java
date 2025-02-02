package uk.gov.hmcts.reform.juddata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import uk.gov.hmcts.reform.data.ingestion.DataIngestionLibraryRunner;
import uk.gov.hmcts.reform.data.ingestion.camel.service.AuditServiceImpl;

@SpringBootApplication(scanBasePackages = "uk.gov.hmcts.reform")
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
@Slf4j
public class JudicialApplication implements ApplicationRunner {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @Value("${batchjob-name}")
    String jobName;

    private static String logComponentName;

    @Autowired
    DataIngestionLibraryRunner dataIngestionLibraryRunner;

    @Autowired
    AuditServiceImpl judicialAuditServiceImpl;

    public static void main(final String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(JudicialApplication.class);
        //Sleep added to allow app-insights to flush the logs
        Thread.sleep(7000);
        int exitCode = SpringApplication.exit(context);
        log.info("{}:: Judicial Application exiting with exit code {} ", logComponentName, exitCode);
        System.exit(exitCode);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addString(jobName, String.valueOf(System.currentTimeMillis()))
            .toJobParameters();
        dataIngestionLibraryRunner.run(job, params);
    }

    @Value("${logging-component-name}")
    public void setLogComponentName(String logComponentName) {
        JudicialApplication.logComponentName = logComponentName;
    }
}
