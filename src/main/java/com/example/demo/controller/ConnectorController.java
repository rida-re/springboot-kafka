package com.example.demo.controller;


import com.example.demo.kafka.service.ConnectorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connectors")
public class ConnectorController {

    private final ConnectorService connectorService;

    public ConnectorController(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    @GetMapping
    public String listConnectors() {
        return connectorService.listConnectors();
    }

    @GetMapping("/{name}")
    public String getConnector(@PathVariable String name) {
        return connectorService.getConnectorInfo(name);
    }

    @PostMapping
    public String createConnector(@RequestBody String connectorJson) {
        return connectorService.createConnector(connectorJson);
    }

    @DeleteMapping("/{name}")
    public String deleteConnector(@PathVariable String name) {
        connectorService.deleteConnector(name);
        return "Deleted connector: " + name;
    }
}
