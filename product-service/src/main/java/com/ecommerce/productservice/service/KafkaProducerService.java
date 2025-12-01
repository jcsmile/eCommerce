package com.ecommerce.productservice.service;


import com.ecommerce.productservice.event.ProductStockEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for producing Kafka messages related to product stock updates.
 * <p>
 * Business rules:
 * 1. Sends events to product-stock-updated topic
 * 2. Logs sent events
 *
 * @author JackyChen
 * @since 2025-04-01
 */
@Service
public class KafkaProducerService {
    private static final org.slf4j.Logger log
            = org.slf4j.LoggerFactory.getLogger(KafkaProducerService.class);

    private KafkaTemplate<String, ProductStockEvent> kafkaTemplate;
    private static final String STOCK_TOPIC = "product-stock-updated";

    /**
     * Constructor for KafkaProducerService.
     * <p>
     * Business rules:
     * 1. Injects KafkaTemplate
     *
     * @param kafkaTemplate the Kafka template
     * @author JackyChen
     * @since 2025-04-01
     */
    public KafkaProducerService (KafkaTemplate<String, ProductStockEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a stock update event to Kafka.
     * <p>
     * Business rules:
     * 1. Creates ProductStockEvent and sends to topic
     * 2. Logs the sent event
     *
     * @param productId the product ID
     * @param newStock the new stock quantity
     * @param action the action type (CREATE, UPDATE, DELETE)
     * @author JackyChen
     * @since 2025-04-01
     */
    public void sendStockUpdateEvent(Long productId, int newStock, String action) {
        ProductStockEvent event = new ProductStockEvent(productId, newStock, action);
        kafkaTemplate.send(STOCK_TOPIC, String.valueOf(productId), event);
        log.info("Sent stock update event to Kafka: {}", event);
    }
}
