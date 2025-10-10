package com.ecommerce.productservice.repo;

import com.ecommerce.productservice.domain.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByCategoryContainingIgnoreCase(String category);
    Flux<Product> findByNameContainingIgnoreCase(String name);
}
