package com.example.demo.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectorService {

    private final RestTemplate restTemplate;

    @Value("${spring.connect.url}")
    private String kafkaConnectUrl;

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
