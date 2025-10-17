package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String id;
    private String key;
    private String name;
    private String description;
    private String slug;
    private String sku;
    private String productTypeId;
    private List<String> categoryIds;
    private PriceInfo price;
    private ImageInfo image;
    private Map<String, Object> attributes;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceInfo {
        private String currencyCode;
        private Long centAmount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageInfo {
        private String url;
        private String label;
        private Dimensions dimensions;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Dimensions {
            private Integer w;
            private Integer h;
        }
    }
}