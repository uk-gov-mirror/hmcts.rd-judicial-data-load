package uk.gov.hmcts.reform.elinks.service;

import uk.gov.hmcts.reform.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.juddata.exception.JudicialDataLoadException;

import java.util.Set;

public interface IdamTokenService {

    String getBearerToken() throws JudicialDataLoadException;

    Set<IdamResponse> getSyncFeed() throws JudicialDataLoadException;
}
