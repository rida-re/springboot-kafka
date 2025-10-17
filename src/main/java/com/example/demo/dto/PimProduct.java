package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PimProduct {
    
    @JsonProperty("payload")
    private ProductPayload payload;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductPayload {
        private Long id;
        private String sku;
        private String name;
        private String description;
    }
}
