package com.example.demo.kafka.consumer;

import com.example.demo.dto.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topics.order-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(OrderRequest order) {
        log.info(String.format("Consumed order event => %s", order.toString()));
        // Add your business logic here
    }
}
