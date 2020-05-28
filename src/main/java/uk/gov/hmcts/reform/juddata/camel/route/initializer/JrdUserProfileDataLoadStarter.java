package uk.gov.hmcts.reform.juddata.camel.route.initializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.juddata.camel.route.LoadRoutes;

@Component
@Slf4j
public class JrdUserProfileDataLoadStarter {

    @Autowired
    CamelContext camelContext;

    @Autowired
    LoadRoutes loadRoutes;

//    @PostConstruct
//    public void postConstruct() throws Exception {
//        camelContext.start();
//        camelContext.getGlobalOptions().put(ORCHESTRATED_ROUTE, JUDICIAL_USER_PROFILE_ORCHESTRATION);
//        loadRoutes.startRoute();
//    }
}
