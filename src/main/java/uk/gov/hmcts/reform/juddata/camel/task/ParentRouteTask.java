package uk.gov.hmcts.reform.juddata.camel.task;

import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.JUDICIAL_USER_PROFILE_ORCHESTRATION;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.ORCHESTRATED_ROUTE;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.route.LoadRoutes;
import uk.gov.hmcts.reform.juddata.camel.util.JrdExecutor;

@Component
@Slf4j
public class ParentRouteTask implements Tasklet {

    @Value("${start-route}")
    private String startRoute;

    @Autowired
    CamelContext camelContext;

    @Autowired
    JrdExecutor jrdExecutor;

    @Autowired
    LoadRoutes loadRoutes;

    @Value("${routes-to-execute-orchestration}")
    List<String> routesToExecute;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("::ParentRouteTask starts::");
        camelContext.getGlobalOptions().put(ORCHESTRATED_ROUTE, JUDICIAL_USER_PROFILE_ORCHESTRATION);
        loadRoutes.startRoute(startRoute, routesToExecute);
        jrdExecutor.execute(camelContext, JUDICIAL_USER_PROFILE_ORCHESTRATION, startRoute);
        log.info("::ParentRouteTask completes::");
        return RepeatStatus.FINISHED;
    }
}
