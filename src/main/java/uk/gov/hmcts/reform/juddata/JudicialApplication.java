package uk.gov.hmcts.reform.juddata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import uk.gov.hmcts.reform.data.ingestion.camel.service.AuditServiceImpl;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.juddata.camel.util.JrdDataIngestionLibraryRunner;

@SpringBootApplication(scanBasePackages = "uk.gov.hmcts.reform")
// Spring needs a constructor, its not a utility class
@Slf4j
@EnableFeignClients(basePackages = {
    "uk.gov.hmcts.reform.juddata"}, basePackageClasses = {IdamApi.class})

@SuppressWarnings("all")
public class JudicialApplication  {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @Value("${batchjob-name}")
    String jobName;

    private static String logComponentName;

    @Autowired
    JrdDataIngestionLibraryRunner dataIngestionLibraryRunner;

    @Autowired
    AuditServiceImpl judicialAuditServiceImpl;

    public static void main(final String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(JudicialApplication.class);

    }



    @Value("${logging-component-name}")
    public void setLogComponentName(String logComponentName) {
        JudicialApplication.logComponentName = logComponentName;
    }
}
