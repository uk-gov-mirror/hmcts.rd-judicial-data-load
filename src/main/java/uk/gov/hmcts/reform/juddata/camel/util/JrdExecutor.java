package uk.gov.hmcts.reform.juddata.camel.util;

import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.ERROR_MESSAGE;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.service.EmailService;
import uk.gov.hmcts.reform.data.ingestion.camel.util.DataLoadUtil;
import uk.gov.hmcts.reform.data.ingestion.camel.util.RouteExecutor;
import uk.gov.hmcts.reform.juddata.camel.service.AuditProcessingService;

@Slf4j
@Component
@RefreshScope
public class JrdExecutor extends RouteExecutor {

    @Autowired
    CamelContext camelContext;

    @Autowired
    DataLoadUtil dataLoadUtil;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    AuditProcessingService auditProcessingService;

    @Autowired
    EmailService emailService;

    public void execute(CamelContext camelContext, String schedulerName, String route) {
        try {
            super.execute(camelContext, schedulerName, route);
        } catch (Exception ex) {
            //Camel override error stack with route failed hence grabbing exception form context
            String errorMessage = camelContext.getGlobalOptions().get(ERROR_MESSAGE);
            auditProcessingService.auditException(camelContext, errorMessage);
            log.error(":: " + schedulerName + " failed:: {}", errorMessage);
        } finally {
            //runs Job Auditing
            auditProcessingService.auditSchedulerStatus(camelContext);
        }
    }
}
