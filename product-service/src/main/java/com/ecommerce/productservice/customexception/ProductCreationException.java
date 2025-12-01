package com.ecommerce.productservice.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when product creation fails.
 */
public class ProductCreationException extends ResponseStatusException {
    /**
     * Constructs a new ProductCreationException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ProductCreationException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause); // HTTP 500
    }
}
