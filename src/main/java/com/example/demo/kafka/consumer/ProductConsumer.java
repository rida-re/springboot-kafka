package com.example.demo.kafka.consumer;

import com.example.demo.dto.PimProduct;
import com.example.demo.dto.ProductRequest;
import com.example.demo.kafka.service.ProductApiClientService;
import com.example.demo.kafka.service.ProductApiImportService;
import com.example.demo.kafka.service.ProductApiSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductConsumer {

    private final ProductApiSyncService productApiSyncService;
    private final ProductApiImportService productApiImportService;
    private final ProductApiClientService productApiClientService;

    @KafkaListener(
            topics = "${spring.kafka.topics.product-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ProductRequest product) {
        log.info(String.format("Consumed product event => %s", product.toString()));
        try {
            // Use Import API for product creation/update
            productApiImportService.importProduct(product);
            log.info("Product import initiated for product: {}", product.getKey());
        } catch (Exception e) {
            log.error("Error importing product: {}", product.getKey(), e);
            // Fallback to Sync API if import fails
            try {
                productApiSyncService.syncProduct(product);
                log.info("Fallback: Product synchronization initiated for product: {}", product.getKey());
            } catch (Exception syncEx) {
                log.error("Error synchronizing product: {}", product.getKey(), syncEx);
            }
        }
    }

  
   /* @KafkaListener(
            topics = "${spring.kafka.topics.products}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(@Payload PimProduct product) {
        try {
            log.debug("Raw message received: {}", product);

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
            productApiClientService.importProduct(product);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
    } */
}
