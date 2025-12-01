package com.ecommerce.productservice.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a product is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
    /**
     * Constructs a new ProductNotFoundException with the specified product ID.
     *
     * @param id the product ID that was not found
     */
    public ProductNotFoundException(Long id) {
        super("Product not found with id " + id);
    }
}

