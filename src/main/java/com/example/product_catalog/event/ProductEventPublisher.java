package com.example.product_catalog.event;

import com.example.product_catalog.model.Product;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProductEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "product-exchange";
    private static final String ROUTING_KEY = "product.created.updated";

    public void publishProductCreatedOrUpdatedEvent(Product product, String eventType) {
        EventPayload payload = EventPayload.builder()
                .eventType(eventType)
                .timestamp(Instant.now().toString())
                .product(product)
                .build();

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, payload);
     System.out.println("Published event to RabbitMQ: " + payload);
    }
}
