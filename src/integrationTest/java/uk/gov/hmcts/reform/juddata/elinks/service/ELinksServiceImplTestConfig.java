package uk.gov.hmcts.reform.juddata.elinks.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.elinks.feign.ElinksFeignClient;

@TestConfiguration
public class ELinksServiceImplTestConfig {
    @MockBean
    ElinksFeignClient elinksFeignClient;
}
