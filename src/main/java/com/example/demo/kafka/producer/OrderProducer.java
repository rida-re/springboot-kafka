package com.example.demo.kafka.producer;

import com.example.demo.dto.OrderRequest;
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
public class OrderProducer {

    @Value("${spring.kafka.topics.order-topic}")
    private String topicName;

    private final KafkaTemplate<String, OrderRequest> kafkaTemplate;

     public void sendMessage(OrderRequest order) {
        log.info(String.format("Producing order event => %s", order.toString()));

        Message<OrderRequest> message = MessageBuilder
                .withPayload(order)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();

        kafkaTemplate.send(message);
    }
}
