package com.example.demo.kafka.producer;

import com.example.demo.dto.ProductRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class ProductProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductProducer.class);

    @Value("${spring.kafka.topics.product-topic}")
    private String topicName;

    private final KafkaTemplate<String, ProductRequest> kafkaTemplate;

    public ProductProducer(KafkaTemplate<String, ProductRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(ProductRequest product) {
        LOGGER.info(String.format("Producing product event => %s", product.toString()));

        Message<ProductRequest> message = MessageBuilder
                .withPayload(product)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

        kafkaTemplate.send(message);
    }

}
