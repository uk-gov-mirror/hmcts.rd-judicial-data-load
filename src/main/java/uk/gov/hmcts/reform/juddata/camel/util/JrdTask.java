package uk.gov.hmcts.reform.juddata.camel.util;

import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.ERROR_MESSAGE;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.IS_EXCEPTION_HANDLED;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.SCHEDULER_STATUS;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.juddata.camel.service.AuditProcessingService;
import uk.gov.hmcts.reform.juddata.camel.service.EmailService;

@Slf4j
@Component
@RefreshScope
public class JrdTask {

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
            camelContext.getGlobalOptions().remove(IS_EXCEPTION_HANDLED);
            camelContext.getGlobalOptions().remove(SCHEDULER_STATUS);
            dataLoadUtil.setGlobalConstant(camelContext, schedulerName);
            producerTemplate.sendBody(route, "starting " + schedulerName);
        } catch (Exception ex) {
            //Camel override error stack with route failed hence grabbing exception form context
            String errorMessage = camelContext.getGlobalOptions().get(ERROR_MESSAGE);
            auditProcessingService.auditException(camelContext, errorMessage);
            log.error(":: " + schedulerName + " failed::", errorMessage);
            //check mail flag and send mail
            emailService.sendEmail(errorMessage);
        } finally {
            //runs Job Auditing
            auditProcessingService.auditSchedulerStatus(camelContext);
        }
    }
}
