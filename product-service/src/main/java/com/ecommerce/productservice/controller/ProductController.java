package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.customexception.ProductNotFoundException;
import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.service.ProductService;
import com.ecommerce.productservice.util.DataInitializer;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.*;

import java.net.URI;

/**
 * REST Controller for Product operations.
 * <p>
 * Business rules:
 * 1. Handles HTTP requests for product CRUD operations
 * 2. Supports pagination and search
 * 3. Requires admin role for create operations
 *
 * @author JackyChen
 * @since 2025-04-01
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final DataInitializer dataInitializer;

    /**
     * Constructor for ProductController.
     * <p>
     * Business rules:
     * 1. Injects ProductService and DataInitializer
     *
     * @param productService the product service
     * @param dataInitializer the data initializer
     * @author JackyChen
     * @since 2025-04-01
     */
    ProductController (ProductService productService, DataInitializer dataInitializer) {
        this.productService = productService;
        this.dataInitializer = dataInitializer;
    }

    /**
     * Retrieves all products with pagination.
     * <p>
     * Business rules:
     * 1. Supports optional page and size parameters
     * 2. Defaults to page 0 and size 10
     *
     * @param page the page number (optional)
     * @param size the page size (optional)
     * @return a Flux of ProductDto
     * @author JackyChen
     * @since 2025-04-01
     */
    @GetMapping
    public Flux<ProductDto> listAll(@Param("page") Integer page, @Param("size") Integer size) {
        return productService.getAll(page!=null? page : 0, size!=null? size : 10);
    }

    /**
     * Retrieves a product by ID.
     * <p>
     * Business rules:
     * 1. Returns 200 OK if found
     * 2. Returns 404 Not Found if not found
     *
     * @param id the product ID
     * @return a Mono of ResponseEntity with ProductDto
     * @author JackyChen
     * @since 2025-04-01
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductDto>> getOne(@PathVariable Long id) {
        return productService.getById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Searches products by keyword.
     * <p>
     * Business rules:
     * 1. Searches in product names
     * 2. Case insensitive search
     *
     * @param keyword the search keyword
     * @return a Flux of ProductDto
     * @author JackyChen
     * @since 2025-04-01
     */
    @GetMapping("/search")
    public Flux<ProductDto> search(@RequestParam String keyword) {
        return productService.search(keyword);
    }

    /**
     * Creates a new product.
     * <p>
     * Business rules:
     * 1. Requires ADMIN role
     * 2. Returns 201 Created on success
     * 3. Handles validation and duplicate errors
     *
     * @param productDto the product data
     * @return a Mono of ResponseEntity with created ProductDto
     * @author JackyChen
     * @since 2025-04-01
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ProductDto>> create(@RequestBody ProductDto productDto) {
        return productService.create(productDto)
                .map(saved -> ResponseEntity
                        .created(URI.create("/api/products/" + saved.getId()))
                        .body(saved))
                .onErrorResume(e -> {
                    if (e instanceof org.springframework.web.server.ResponseStatusException rse) {
                        return Mono.just(ResponseEntity.status(rse.getStatusCode()).body(null));
                    }
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }


    /**
     * Deletes a product by ID.
     * <p>
     * Business rules:
     * 1. Returns 204 No Content on success
     * 2. Returns 404 Not Found if product not found
     *
     * @param id the product ID
     * @return a Mono of ResponseEntity
     * @author JackyChen
     * @since 2025-04-01
     */
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        return productService.delete(id)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .onErrorResume(ProductNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Initializes demo products.
     * <p>
     * Business rules:
     * 1. Clears existing products
     * 2. Inserts sample data
     *
     * @return a Mono of Void
     * @author JackyChen
     * @since 2025-04-01
     */
    @PostMapping("/init")
    //@PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> init() {
        return dataInitializer.initProducts();
    }
}
