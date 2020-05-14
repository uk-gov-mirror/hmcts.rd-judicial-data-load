package uk.gov.hmcts.reform.juddata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "uk.gov.hmcts.reform.juddata")
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
@Slf4j
public class JudicialApplication {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @Value("${batchjob-name}")
    String jobName;
    
    public static void main(final String[] args) {
        ApplicationContext context = SpringApplication.run(JudicialApplication.class);
        int exitCode = SpringApplication.exit(context);
        log.info("Judicial Application exiting with exit code " + exitCode);
        System.exit(exitCode);
    }
}
