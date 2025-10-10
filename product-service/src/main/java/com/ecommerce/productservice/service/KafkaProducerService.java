package com.ecommerce.productservice.service;


import com.ecommerce.productservice.event.ProductStockEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private static final org.slf4j.Logger log
            = org.slf4j.LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, ProductStockEvent> kafkaTemplate;
    private static final String STOCK_TOPIC = "product-stock-updated";

    KafkaProducerService (KafkaTemplate<String, ProductStockEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStockUpdateEvent(Long productId, int newStock, String action) {
        ProductStockEvent event = new ProductStockEvent(productId, newStock, action);
        kafkaTemplate.send(STOCK_TOPIC, String.valueOf(productId), event);
        log.info("Sent stock update event to Kafka: {}", event);
    }
}
