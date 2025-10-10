package com.ecommerce.productservice.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateProductException extends ResponseStatusException {
    public DuplicateProductException(String message) {
        super(HttpStatus.CONFLICT, message); // HTTP 409
    }
}
