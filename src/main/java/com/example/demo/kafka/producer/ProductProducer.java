package com.example.demo.kafka.producer;

import com.example.demo.dto.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductProducer {

    @Value("${spring.kafka.topics.product-topic}")
    private String topicName;

    private final KafkaTemplate<String, ProductRequest> kafkaTemplate;

    public void sendMessage(ProductRequest product) {
        log.info(String.format("Producing product event => %s", product.toString()));

        Message<ProductRequest> message = MessageBuilder
                .withPayload(product)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

        kafkaTemplate.send(message);
    }

}
