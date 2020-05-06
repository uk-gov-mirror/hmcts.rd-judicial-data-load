package uk.gov.hmcts.reform.juddata.camel.util;

import static java.util.Collections.sort;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.HEADER_EXCEPTION;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.SCHEDULER_NAME;
import static uk.gov.hmcts.reform.juddata.camel.util.MappingConstants.SCHEDULER_START_TIME;

import io.jsonwebtoken.lang.Collections;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.yaml.snakeyaml.Yaml;
import uk.gov.hmcts.reform.juddata.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.juddata.camel.route.beans.RouteProperties;

@Service
public class HeaderUtil {

    @Autowired
    CamelContext camelContext;

    @Autowired
    @Qualifier("springJdbcTransactionManager")
    PlatformTransactionManager platformTransactionManager;

    @Value("${invalid-header-sql}")
    String invalidHeaderSql;

    @Autowired
    @Qualifier("springJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void checkHeader(Exchange exchange, RouteProperties routeProperties, String exceptionMsg) {
        exchange.getIn().setHeader(HEADER_EXCEPTION, HEADER_EXCEPTION);
        //separate transaction manager required for auditing as it is independent form route
        //Transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("header exception logs");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        String schedulerTime = camelContext.getGlobalOptions().get(SCHEDULER_START_TIME);
        String schedulerName = camelContext.getGlobalOptions().get(SCHEDULER_NAME);
        Object[] params = new Object[]{routeProperties.getFileName(), new Timestamp(Long.valueOf(schedulerTime)),
            schedulerName, exceptionMsg + routeProperties.getBinder(), new Timestamp(new Date().getTime())};
        jdbcTemplate.update(invalidHeaderSql, params);
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        platformTransactionManager.commit(status);
        throw new RouteFailedException(exceptionMsg + routeProperties.getFileName());
    }

    public String getInvalidJrdHeader(List<String> header, String binder) {
        List<String> originalHeader;
        String exceptionMsg = "";
        Map<String, Object> headerMap = readYmlAsMap("header-mapping.yaml");
        String headers = (String) headerMap.get(binder);
        originalHeader = Arrays.asList(headers.split(","));

        sort(header);
        sort(originalHeader);

        if (originalHeader.size() == header.size() || header.size() > originalHeader.size()) {
            List<String> invalidHeaderExtra = header.stream().sorted().filter(o -> (originalHeader.stream().sorted().filter(f -> f.equalsIgnoreCase(o)).count()) < 1).collect(Collectors.toList());
            if (null != invalidHeaderExtra && !Collections.isEmpty(invalidHeaderExtra) && invalidHeaderExtra.toString().replaceAll("\\[\\]", "").length() > 0) {
                exceptionMsg = "Invalid column(s) : " + invalidHeaderExtra.toString().replace(", ]", "]") + ". Please remove invalid column(s) from file.";
            }
        }
        if (header.size() < originalHeader.size()) {
            List<String> invalidHeaderLess = originalHeader.stream().sorted().filter(o -> (header.stream().sorted().filter(f -> f.equalsIgnoreCase(o)).count()) < 1).collect(Collectors.toList());
            if (null != invalidHeaderLess && !Collections.isEmpty(invalidHeaderLess) && invalidHeaderLess.toString().replaceAll("\\[\\]", "").length() > 0) {
                exceptionMsg = "Missing column(s) : " + invalidHeaderLess.toString().replace(", ]", "]") + ". Please add column(s) from file.";
            }
        }
        return exceptionMsg;
    }

    public Map<String, Object> readYmlAsMap(String yamlFile) {
        Yaml yaml = new Yaml();
        InputStream inputStream = HeaderUtil.class.getClassLoader().getResourceAsStream(yamlFile);
        return yaml.load(inputStream);
    }

}
