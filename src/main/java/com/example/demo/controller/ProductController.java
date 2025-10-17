package com.example.demo.controller;

import com.example.demo.dto.ProductRequest;
import com.example.demo.kafka.producer.ProductProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductProducer producer;

    public ProductController(ProductProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductRequest product) {
        producer.sendMessage(product);
        return ResponseEntity.ok("Product event sent successfully");
    }
}
