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

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final DataInitializer dataInitializer;

    ProductController (ProductService productService, DataInitializer dataInitializer) {
        this.productService = productService;
        this.dataInitializer = dataInitializer;
    }

    @GetMapping
    public Flux<ProductDto> listAll(@Param("page") Integer page, @Param("size") Integer size) {
        return productService.getAll(page!=null? page : 0, size!=null? size : 10);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductDto>> getOne(@PathVariable Long id) {
        return productService.getById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/search")
    public Flux<ProductDto> search(@RequestParam String keyword) {
        return productService.search(keyword);
    }

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


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        return productService.delete(id)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .onErrorResume(ProductNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/init")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> init() {
        return dataInitializer.initProducts();
    }
}
