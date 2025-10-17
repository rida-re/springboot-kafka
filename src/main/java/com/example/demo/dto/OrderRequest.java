package com.example.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor       // Default constructor
@AllArgsConstructor      // All-args constructor
public class OrderRequest {
    private String orderId;
    private String product;
    private int quantity;
    private double price;
}
