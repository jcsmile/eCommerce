package com.ecommerce.productservice.service;

import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.repo.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

    @Test
    void getProductById_FallbackTriggered() {
        when(repo.findById(anyLong()))
                .thenThrow(new RuntimeException("DB down"));

        StepVerifier.create(service.getById(99L))
                .expectNextMatches(dto -> dto.getName().equals("Unavailable"))
                .verifyComplete();
    }

    @Test
    void getProductById_ShouldRetryAndFallback() {
        when(repo.findById(anyLong()))
                .thenThrow(new IOException("Simulated I/O failure"));

        StepVerifier.create(service.getById(10L))
                .expectNextMatches(dto -> dto.getName().equals("Unavailable"))
                .verifyComplete();

        verify(repo, times(3)).findById(anyLong()); // retried 3 times
    }

    @Test
    void bulkheadShouldRejectWhenTooManyConcurrentRequests() {
        List<Mono<ProductDto>> calls = IntStream.range(0, 20)
                .mapToObj(i -> service.getById((long) i))
                .toList();

        StepVerifier.create(Flux.merge(calls))
                .expectNextCount(10)  // 10 allowed
                .expectErrorMatches(e -> e.getMessage().contains("Bulkhead"))
                .verify();
    }

}
