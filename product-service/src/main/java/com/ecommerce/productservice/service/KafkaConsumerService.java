package com.ecommerce.productservice.service;

import com.ecommerce.productservice.event.ProductStockEvent;
import com.ecommerce.productservice.repo.ProductRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service for consuming Kafka messages related to product stock updates.
 * <p>
 * Business rules:
 * 1. Listens to payment-success-events topic
 * 2. Updates product stock on successful payments
 * 3. Handles stock reservation
 *
 * @author JackyChen
 * @since 2025-04-01
 */
@Service
public class KafkaConsumerService {
    private static final org.slf4j.Logger log
            = org.slf4j.LoggerFactory.getLogger(KafkaConsumerService.class);


    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Constructor for KafkaConsumerService.
     * <p>
     * Business rules:
     * 1. Injects ProductRepository and KafkaProducerService
     *
     * @param productRepository the product repository
     * @param kafkaProducerService the Kafka producer service
     * @author JackyChen
     * @since 2025-04-01
     */
    public KafkaConsumerService(ProductRepository productRepository, KafkaProducerService kafkaProducerService) {
        this.productRepository = productRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Handles payment success events from Kafka.
     * <p>
     * Business rules:
     * 1. Reserves stock for the product
     * 2. Logs errors if stock is insufficient
     *
     * @param event the ProductStockEvent
     * @author JackyChen
     * @since 2025-04-01
     */
    @KafkaListener(topics = "payment-success-events",
                    groupId = "product-service-group",
                    containerFactory = "productStockEventKafkaListenerContainerFactory")
    public void handlePaymentSuccess(ProductStockEvent event) {
        // Assume "action" = "SOLD"
        Long productId = event.getProductId();
//        productRepository.findById(productId)
//                .flatMap(product -> {
//                    product.setStock(Math.max(0, product.getStock() - 1));
//                    return productRepository.save(product)
//                            .doOnSuccess(saved ->
//                                    kafkaProducerService.sendStockUpdateEvent(saved.getId(), saved.getStock(), "UPDATE"));
//                })
//                .onErrorResume(e -> Mono.empty()) // prevent crash
//                .subscribe();
        productRepository.reserveStock(productId,event.getNewStock())
                .flatMap(updatedRows -> {
                    if (updatedRows <= 0) {
                        log.error("Not enough stock for product ID {}", productId);
                        return Mono.error(new IllegalStateException("Not enough stock"));
                    }
                    return Mono.empty();
                }).onErrorResume(e -> {
                    log.error("Error updating stock for product ID {}: {}", productId, e.getMessage());
                    return Mono.empty();
                }); // prevent crash;

    }

//    @KafkaListener(topics = "test-topic",
//                    groupId = "test-group")
//    public void handleTestMessage(ConsumerRecord<String, Object> record){
//        System.out.println("Received message: " + record.value());
//    }
}
