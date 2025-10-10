package com.ecommerce.productservice.service;

import com.ecommerce.productservice.event.ProductStockEvent;
import com.ecommerce.productservice.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KafkaConsumerService {

    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    KafkaConsumerService(ProductRepository productRepository, KafkaProducerService kafkaProducerService) {
        this.productRepository = productRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "payment-success-events", groupId = "product-service-group")
    public void handlePaymentSuccess(ProductStockEvent event) {
        // Assume "action" = "SOLD"
        Long productId = event.getProductId();
        productRepository.findById(productId)
                .flatMap(product -> {
                    product.setStock(Math.max(0, product.getStock() - 1));
                    return productRepository.save(product)
                            .doOnSuccess(saved ->
                                    kafkaProducerService.sendStockUpdateEvent(saved.getId(), saved.getStock(), "SOLD"));
                })
                .onErrorResume(e -> Mono.empty()) // prevent crash
                .subscribe();
    }
}
