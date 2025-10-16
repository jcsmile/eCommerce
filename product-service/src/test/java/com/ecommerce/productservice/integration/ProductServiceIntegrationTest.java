package com.ecommerce.productservice.integration;

import com.ecommerce.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

class ProductServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ProductService productService;

    //@Test
    void testCreateProductAndFetch() {
        var product = new com.ecommerce.productservice.domain.Product(
                null, "Test Item", "Integration test product", "Misc", 19.99, 10, null);

        var productDto = com.ecommerce.productservice.dto.ProductDto.fromEntity(product);

        StepVerifier.create(productService.create(productDto))
                .expectNextMatches(saved -> saved.getName().equals("Test Item"))
                .verifyComplete();
    }
}
