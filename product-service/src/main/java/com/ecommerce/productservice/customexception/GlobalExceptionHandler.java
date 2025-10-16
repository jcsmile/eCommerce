package com.ecommerce.productservice.customexception;


import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message, String path) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(
            ProductNotFoundException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(),
                exchange.getRequest().getPath().toString());
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateProduct(
            DuplicateProductException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(),
                exchange.getRequest().getPath().toString());
    }

    @ExceptionHandler(ProductCreationException.class)
    public ResponseEntity<Map<String, Object>> handleProductCreation(
            ProductCreationException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                exchange.getRequest().getPath().toString());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(
            ServiceUnavailableException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(),
                exchange.getRequest().getPath().toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                exchange.getRequest().getPath().toString());
    }

    // Fallback for any unexpected exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(
            Exception ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error: " + ex.getMessage(),
                exchange.getRequest().getPath().toString());
    }
}

