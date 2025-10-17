package com.example.demo.kafka.consumer;

import com.example.demo.dto.ProductRequest;
import com.example.demo.kafka.service.ProductImportService;
import com.example.demo.kafka.service.ProductSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);
    private final ProductSyncService productSyncService;
    private final ProductImportService productImportService;

    public ProductConsumer(ProductSyncService productSyncService, ProductImportService productImportService) {
        this.productSyncService = productSyncService;
        this.productImportService = productImportService;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.product-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ProductRequest product) {
        LOGGER.info(String.format("Consumed product event => %s", product.toString()));
        try {
            // Use Import API for product creation/update
            productImportService.importProduct(product);
            LOGGER.info("Product import initiated for product: {}", product.getKey());
        } catch (Exception e) {
            LOGGER.error("Error importing product: {}", product.getKey(), e);
            // Fallback to Sync API if import fails
            try {
                productSyncService.syncProduct(product);
                LOGGER.info("Fallback: Product synchronization initiated for product: {}", product.getKey());
            } catch (Exception syncEx) {
                LOGGER.error("Error synchronizing product: {}", product.getKey(), syncEx);
            }
        }
    }
}
