package com.ecommerce.productservice.service;

import com.ecommerce.productservice.customexception.DuplicateProductException;
import com.ecommerce.productservice.customexception.ProductCreationException;
import com.ecommerce.productservice.customexception.ProductNotFoundException;
import com.ecommerce.productservice.customexception.ValidationException;
import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.repo.ProductRepository;
import com.ecommerce.productservice.service.KafkaProducerService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    ProductService (ProductRepository productRepository, KafkaProducerService kafkaProducerService) {
        this.productRepository = productRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public Flux<ProductDto> getAll(int page, int size) {
        int skip =  page * size;
        return productRepository.findAll()
                .skip(skip)
                .take(size)
                .map(ProductDto::fromEntity);
    }

    public Mono<ProductDto> getById(Long id) {
        return productRepository.findById(id).map(ProductDto::fromEntity);
    }

    public Flux<ProductDto> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).map(ProductDto::fromEntity);
    }

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
}
