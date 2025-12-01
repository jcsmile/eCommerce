package com.ecommerce.productservice.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown for validation errors.
 */
public class ValidationException extends ResponseStatusException {
    /**
     * Constructs a new ValidationException with the specified message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message); // HTTP 400
    }
}
