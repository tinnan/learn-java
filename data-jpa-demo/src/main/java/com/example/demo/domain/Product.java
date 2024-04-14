package com.example.demo.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private Dimension dimension;
    private Double price;

    public Product(String name, String description, Dimension dimension, Double price) {
        this.name = name;
        this.description = description;
        this.dimension = dimension;
        this.price = price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dimension {
        private Double weight;
        private Double width;
        private Double height;
    }
}
