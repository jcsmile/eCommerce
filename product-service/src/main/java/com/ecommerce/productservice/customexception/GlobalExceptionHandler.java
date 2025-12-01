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
import java.util.HashMap;

/**
 * Global exception handler for the Product Service.
 * <p>
 * Business rules:
 * 1. Handles custom exceptions using a generic handler with status mapping
 * 2. Provides error details including timestamp, status, message, and path
 * 3. Maps exceptions to appropriate HTTP status codes via a static map
 * 4. Reduces code duplication by consolidating exception handling logic
 *
 * @author JackyChen
 * @since 2025-04-01
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = new HashMap<>();

    static {
        EXCEPTION_STATUS_MAP.put(ProductNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(DuplicateProductException.class, HttpStatus.CONFLICT);
        EXCEPTION_STATUS_MAP.put(ProductCreationException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_STATUS_MAP.put(ServiceUnavailableException.class, HttpStatus.SERVICE_UNAVAILABLE);
        EXCEPTION_STATUS_MAP.put(IllegalArgumentException.class, HttpStatus.BAD_REQUEST);
    }

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

    /**
     * Generic handler for custom exceptions.
     * <p>
     * Business rules:
     * 1. Determines HTTP status based on exception type
     * 2. Returns standardized error response
     *
     * @param ex the exception
     * @param exchange the server web exchange
     * @return ResponseEntity with error details
     * @author JackyChen
     * @since 2025-04-01
     */
    @ExceptionHandler({ProductNotFoundException.class, DuplicateProductException.class,
                       ProductCreationException.class, ServiceUnavailableException.class,
                       IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleCustomExceptions(
            Exception ex, ServerWebExchange exchange) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildErrorResponse(status, ex.getMessage(),
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

