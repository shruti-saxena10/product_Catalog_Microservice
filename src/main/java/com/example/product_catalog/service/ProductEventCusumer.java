//package com.example.product_catalog.service;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ProductEventCusumer {
//    @RabbitListener(queues = "product-queue")
//    public void listenForProductEvent(String message) {
//        // This method will be triggered when a message is consumed from the queue
//        System.out.println("Received message: " + message);
//    }
//}
