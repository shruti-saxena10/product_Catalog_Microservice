package com.example.product_catalog.dto;

import com.example.product_catalog.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEvent {
    private String eventType;
    private Instant timestamp = Instant.now();
    private Product product;

    public ProductEvent(String eventType, Product product) {
        this.eventType = eventType;
        this.product = product;
        this.timestamp = Instant.now();
    }

}
