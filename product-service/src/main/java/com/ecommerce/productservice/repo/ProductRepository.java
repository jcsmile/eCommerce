package com.ecommerce.productservice.repo;

import com.ecommerce.productservice.domain.Product;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Product entities.
 * Provides reactive CRUD operations and custom queries for products.
 */
@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    /**
     * Finds products by category containing the given string, ignoring case.
     *
     * @param category the category to search for
     * @return a Flux of products matching the category
     */
    Flux<Product> findByCategoryContainingIgnoreCase(String category);

    /**
     * Finds products by name containing the given string, ignoring case.
     *
     * @param name the name to search for
     * @return a Flux of products matching the name
     */
    Flux<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Reserves stock for a product by reducing the stock quantity.
     * Only updates if sufficient stock is available.
     *
     * @param id the product ID
     * @param quantity the quantity to reserve
     * @return a Mono of the number of affected rows
     */
    @Modifying
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity")
    Mono<Integer> reserveStock(Long id, int quantity);
}
