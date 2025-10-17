package com.example.demo.kafka.producer;

import com.example.demo.dto.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

    @Value("${spring.kafka.topics.order-topic}")
    private String topicName;

    private final KafkaTemplate<String, OrderRequest> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(OrderRequest order) {
        LOGGER.info(String.format("Producing order event => %s", order.toString()));

        Message<OrderRequest> message = MessageBuilder
                .withPayload(order)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

        kafkaTemplate.send(message);
    }
}
