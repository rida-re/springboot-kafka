package com.example.demo.config;

import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;

@Configuration
public class CommerceToolsClientConfig {

    @Value("${commercetools.projectKey}")
    private String projectKey;

    @Value("${commercetools.clientId}")
    private String clientId;

    @Value("${commercetools.clientSecret}")
    private String clientSecret;

    @Bean
    public ProjectApiRoot createClient() {
        return ApiRootBuilder.of()
            .defaultClient(ClientCredentials.of()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .build(),
                ServiceRegion.GCP_EUROPE_WEST1)
            .build(projectKey);
    }
}
