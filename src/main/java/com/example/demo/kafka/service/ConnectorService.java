package com.example.demo.kafka.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConnectorService {

    private final RestTemplate restTemplate;

    @Value("${spring.connect.url}")
    private String kafkaConnectUrl;

    public ConnectorService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String listConnectors() {
        return restTemplate.getForObject(kafkaConnectUrl + "/connectors", String.class);
    }

    public String getConnectorInfo(String name) {
        return restTemplate.getForObject(kafkaConnectUrl + "/connectors/" + name, String.class);
    }

    public String createConnector(String connectorJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(connectorJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(kafkaConnectUrl + "/connectors", entity, String.class);
        return response.getBody();
    }

    public void deleteConnector(String name) {
        restTemplate.delete(kafkaConnectUrl + "/connectors/" + name);
    }
}
