package com.ecommerce.productservice.service;

import com.ecommerce.productservice.customexception.*;
import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.repo.ProductRepository;
import com.ecommerce.productservice.service.KafkaProducerService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for managing products.
 * Provides business logic for product operations including CRUD, search, and stock management.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Constructor for ProductService.
     *
     * @param productRepository the product repository
     * @param kafkaProducerService the Kafka producer service
     */
    ProductService (ProductRepository productRepository, KafkaProducerService kafkaProducerService) {
        this.productRepository = productRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Retrieves all products with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a Flux of ProductDto
     */
    public Flux<ProductDto> getAll(int page, int size) {
        int skip =  page * size;
        return productRepository.findAll()
                .skip(skip)
                .take(size)
                .map(ProductDto::fromEntity);
    }

    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "fallbackGetProductById")
    @Retry(name = "productServiceCB")
    @RateLimiter(name = "productServiceCB")
    @Bulkhead(name = "productServiceCB",type = Bulkhead.Type.THREADPOOL)
    public Mono<ProductDto> getById(Long id) {
        return productRepository.findById(id).map(ProductDto::fromEntity);
    }

    public Flux<ProductDto> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).map(ProductDto::fromEntity);
    }

    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "fallbackCreate")
    @Retry(name = "productServiceCB")
    @RateLimiter(name = "productServiceCB")
    @Bulkhead(name = "productServiceCB",type = Bulkhead.Type.THREADPOOL)
    public Mono<ProductDto> create(ProductDto dto) {
        Product entity = new Product(dto.getId()
                ,dto.getName()
                ,dto.getDescription()
                ,dto.getCategory()
                ,dto.getPrice()
                ,dto.getStock()
                ,dto.getImageUrl());

        // Basic input validation
        if (entity.getName() == null || entity.getPrice() == null) {
            return Mono.error(new IllegalArgumentException("Product name and price are required"));
        }

        return productRepository.save(entity)
                .doOnSuccess(saved ->
                        kafkaProducerService.sendStockUpdateEvent(saved.getId(), saved.getStock(), "CREATE"))
                .map(ProductDto::fromEntity)
                .onErrorMap(DataIntegrityViolationException.class,
                        e -> new DuplicateProductException("A product with the same name or unique constraint already exists"))
                .onErrorResume(IllegalArgumentException.class,
                        e -> Mono.error(new ValidationException(e.getMessage())))
                .onErrorMap(e -> new ProductCreationException("Failed to save product", e));
    }


    public Mono<Void> delete(Long id) {
        return productRepository.findById(id)

                .flatMap(product -> productRepository.deleteById(id)
                                            .doOnSuccess(v ->
                                                    kafkaProducerService.sendStockUpdateEvent(product.getId(), 0, "DELETE")))
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)));
    }

    public Mono<ProductDto> updateStock(Long id, int newStock) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(existing -> {
                    existing.setStock(newStock);
                    return productRepository.save(existing)
                            .doOnSuccess(saved ->
                                    kafkaProducerService.sendStockUpdateEvent(saved.getId(), saved.getStock(), "UPDATE"));
                })
                .map(ProductDto::fromEntity);
    }

    public Mono<Void> reserveStock(Long productId, int quantity) {
        return productRepository.reserveStock(productId, quantity)
                .flatMap(rows -> {
                    if (rows == 0) {
                        return Mono.error(new IllegalStateException("Not enough stock"));
                    }
                    return Mono.empty();
                });
    }
    // Fallbacks (must have same return type)
    private Mono<ProductDto> fallbackGetProductById(Long id, Throwable t) {
        Product p = new Product(null,
                "Unavailable",
                "Service degraded",
                "N/A",
                0.0,
                0,
                null);
        return Mono.just(ProductDto.fromEntity(p));
    }

    private Mono<ProductDto> fallbackCreate(Product product, Throwable ex) {
        return Mono.error(new ServiceUnavailableException(
                "ProductService temporarily unavailable: " + ex.getMessage()));
    }
}
