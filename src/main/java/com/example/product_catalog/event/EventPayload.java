package com.example.product_catalog.event;

import com.example.product_catalog.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPayload implements Serializable {
    private String eventType;
    private String timestamp;
    private Product product;
}
