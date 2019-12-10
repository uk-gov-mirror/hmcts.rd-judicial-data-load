package uk.gov.hmcts.reform.judicialapi.configuration;

import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class StorageCredentials {

    @Autowired
    AzureBlobConfig azureBlobConfig;

    @Bean(name = "credsreg")
    public com.microsoft.azure.storage.StorageCredentials credentials()
    {


     /*   CloudBlobClient cloudBlobClient = new CloudBlobClient(URI.create(azureBlobConfig.getBlobUrl()));
        Iterable<CloudBlobContainer> cloudBlobContainers = cloudBlobClient.listContainers("");
        cloudBlobContainers.iterator().next().listBlobs();*/
        return new StorageCredentialsAccountAndKey(azureBlobConfig.getAccountName(), azureBlobConfig.getAccountKey());

    }
}
