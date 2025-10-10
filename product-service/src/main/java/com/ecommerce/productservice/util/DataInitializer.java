package com.ecommerce.productservice.util;

import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

/**
 * Initializes sample product data.
 *
 * Notes:
 *  - This implementation uses new Product(...) constructor calls instead of Product.builder()
 *    to avoid Lombok @Builder related compilation errors if Lombok is not configured.
 *  - Make sure Product has a matching constructor (e.g. @AllArgsConstructor or explicit constructor).
 */
@Component
public class DataInitializer {

    private final ProductRepository productRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataInitializer.class);

    DataInitializer (ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    /**
     * Initialize demo products. Returns a Mono that completes when initialization finishes.
     */
    public Mono<Void> initProducts() {
        List<Product> demoProducts = List.of(
                new Product(null, "Samsung Galaxy S23", "Latest Samsung smartphone", "Electronics", 899.99, 40, "https://picsum.photos/200/200?0"),
                new Product(null, "iPhone 15", "Latest Apple smartphone", "Electronics", 999.99, 50, "https://picsum.photos/200/200?1"),
                new Product(null, "MacBook Air M3", "Lightweight laptop", "Electronics", 1499.99, 30, "https://picsum.photos/200/200?2"),
                new Product(null, "Running Shoes", "Comfortable sports shoes", "Fashion", 89.99, 100, "https://picsum.photos/200/200?3"),
                new Product(null, "Gaming Chair", "Ergonomic chair", "Furniture", 199.99, 20, "https://picsum.photos/200/200?4")
        );

        return productRepository.deleteAll()
                .thenMany(Flux.fromIterable(demoProducts))
                .flatMap(productRepository::save)
                .then()
                .doOnSuccess(v -> log.info("Demo products initialized"))
                .doOnError(e -> log.error("Failed to initialize demo products", e));
    }

    /**
     * Optional: run initialization at startup. If you prefer manual init via an endpoint, remove this.
     */
//    @PostConstruct
//    public void initializeOnStartup() {
//        initProducts()
//                .doOnError(e -> log.error("Product init failed at startup", e))
//                .subscribe(); // subscribe to run the flow
//    }
}
