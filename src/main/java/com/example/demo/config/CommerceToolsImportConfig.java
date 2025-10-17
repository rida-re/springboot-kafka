package com.example.demo.config;

import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.defaultconfig.ImportApiRootBuilder;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommerceToolsImportConfig {

    @Value("${commercetools.projectKey}")
    private String projectKey;

    @Value("${commercetools.clientId}")
    private String clientId;

    @Value("${commercetools.clientSecret}")
    private String clientSecret;

    @Bean
    public ProjectApiRoot createImportClient() {
        return ImportApiRootBuilder.of()
            .defaultClient(ClientCredentials.of()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .build())
            .build(projectKey);
    }
}
