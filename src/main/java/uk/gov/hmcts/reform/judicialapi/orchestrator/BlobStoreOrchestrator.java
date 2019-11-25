package uk.gov.hmcts.reform.judicialapi.orchestrator;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;

public interface BlobStoreOrchestrator {

    void execute() throws IOException, InvalidKeyException, NoSuchProviderException;

}
