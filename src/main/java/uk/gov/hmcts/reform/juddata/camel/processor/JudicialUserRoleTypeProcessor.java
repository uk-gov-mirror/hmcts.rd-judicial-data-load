package uk.gov.hmcts.reform.juddata.camel.processor;

import static java.util.Collections.singletonList;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.JsrValidationBaseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.juddata.camel.binder.JudicialUserRoleType;

@Component
@Slf4j
public class JudicialUserRoleTypeProcessor extends JsrValidationBaseProcessor<JudicialUserRoleType> {


    @Autowired
    JsrValidatorInitializer<JudicialUserRoleType> judicialUserRoleTypeJsrValidatorInitializer;

    @SuppressWarnings("unchecked")
    @Override
    public void process(Exchange exchange) {

        List<JudicialUserRoleType> judicialUserRoleTypes;

        judicialUserRoleTypes = (exchange.getIn().getBody() instanceof List)
                ? (List<JudicialUserRoleType>) exchange.getIn().getBody()
                : singletonList((JudicialUserRoleType) exchange.getIn().getBody());

        log.info("Role type Records count before Validation:: " + judicialUserRoleTypes.size());
        List<JudicialUserRoleType> filteredJudicialRoleTypes = validate(judicialUserRoleTypeJsrValidatorInitializer,
                judicialUserRoleTypes);
        log.info("::Role type Records count after Validation:: " + filteredJudicialRoleTypes.size());
        audit(judicialUserRoleTypeJsrValidatorInitializer, exchange);

        exchange.getMessage().setBody(filteredJudicialRoleTypes);
    }
}
