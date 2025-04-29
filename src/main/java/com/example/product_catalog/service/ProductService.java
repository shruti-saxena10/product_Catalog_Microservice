package com.example.product_catalog.service;

import com.example.product_catalog.config.RabbitMQConfig;
import com.example.product_catalog.event.EventPayload;
import com.example.product_catalog.event.ProductEventPublisher;
import com.example.product_catalog.model.Product;
import com.example.product_catalog.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service

public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;
    private final RabbitTemplate rabbitTemplate;


    public Product createProduct(Product product) {
        product.setLastUpdated(Instant.now());
        Product savedProduct = productRepository.save(product);
        eventPublisher.publishProductCreatedOrUpdatedEvent(savedProduct, "PRODUCT_CREATED");
        return savedProduct;
    }

    public Product updateProduct(UUID id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        product.setAvailableStock(productDetails.getAvailableStock());
        product.setLastUpdated(Instant.now());

        Product updatedProduct = productRepository.save(product);
        eventPublisher.publishProductCreatedOrUpdatedEvent(updatedProduct, "PRODUCT_UPDATED");
        return updatedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }
    public ProductService(ProductRepository productRepository, ProductEventPublisher eventPublisher, RabbitTemplate rabbitTemplate) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.rabbitTemplate = rabbitTemplate;  // Initialize the RabbitTemplate
    }

    // Publish event to the RabbitMQ exchange
    public void publishProductEvent(EventPayload eventPayload) {
        // Publish the event to the exchange with the routing key "product.created"
        rabbitTemplate.convertAndSend("product-exchange", "product.created", eventPayload);
    }
}
