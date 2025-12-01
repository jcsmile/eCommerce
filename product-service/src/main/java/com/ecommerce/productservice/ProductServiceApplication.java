package com.ecommerce.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Product Service.
 * <p>
 * Business rules:
 * 1. Initializes Spring Boot application
 * 2. Loads configuration from application.properties
 * 3. Starts embedded server
 *
 * @author JackyChen
 * @since 2025-04-01
 */
@SpringBootApplication
public class ProductServiceApplication {
    /**
     * Starts the Spring Boot application.
     * <p>
     * Business rules:
     * 1. Runs the application with provided arguments
     *
     * @param args command line arguments
     * @author JackyChen
     * @since 2025-04-01
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
