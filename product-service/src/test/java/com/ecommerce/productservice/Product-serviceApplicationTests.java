package com.ecommerce.productservice.service;

import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.repo.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    ProductRepository repo;

    @InjectMocks
    ProductService service;

    public ProductServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_ShouldReturnProducts() {
        Product p1 = new Product(1L, "Phone", "Smartphone", "Electronics", 999.0, 10, "");
        Product p2 = new Product(2L, "Laptop", "Gaming laptop", "Electronics", 1500.0, 5, "");

        when(repo.findAll()).thenReturn(Flux.just(p1, p2));

        StepVerifier.create(service.getAll(0,10))
                .expectNextCount(2)
                .verifyComplete();
    }
}
