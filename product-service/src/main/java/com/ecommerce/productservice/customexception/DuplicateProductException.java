package com.ecommerce.productservice.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when attempting to create a duplicate product.
 */
public class DuplicateProductException extends ResponseStatusException {
    /**
     * Constructs a new DuplicateProductException with the specified message.
     *
     * @param message the detail message
     */
    public DuplicateProductException(String message) {
        super(HttpStatus.CONFLICT, message); // HTTP 409
    }
}
