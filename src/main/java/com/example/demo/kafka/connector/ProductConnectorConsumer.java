package com.example.demo.kafka.connector;

import com.example.demo.dto.PimProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductConnectorConsumer {

    private final ProductConnectorImportService productImportService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.topics.products}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(@Payload PimProduct product) {
        try {
            log.debug("Raw message received: {}", product);
            //PimProduct product = objectMapper.readValue(rawMessage, PimProduct.class);
            
            if(product == null) {
                log.warn("Received null product message");
                return;
            }
            
            // Validate required fields
            if (product.getPayload().getName() == null || product.getPayload().getSku() == null) {
                log.error("Required product fields are missing. Product: {}", product);
                return;
            }

            log.info("Deserialized product: {}", product);
            productImportService.importProduct(product);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
    }
}
