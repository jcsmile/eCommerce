package com.ecommerce.productservice.customexception;

/**
 * Exception thrown when the service is temporarily unavailable.
 */
public class ServiceUnavailableException extends RuntimeException {
    /**
     * Constructs a new ServiceUnavailableException with the specified message.
     *
     * @param message the detail message
     */
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
