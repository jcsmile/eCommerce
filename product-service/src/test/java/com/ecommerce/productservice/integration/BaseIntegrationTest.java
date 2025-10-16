package com.ecommerce.productservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Base integration test configuration that provides:
 * - PostgreSQL container for database
 * - Kafka container for messaging
 *
 * Extend this class for any integration test in product-service.
 */
//@SpringBootTest
//@Testcontainers
public abstract class BaseIntegrationTest {

    // 🐘 PostgreSQL container
    //@Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.2")
                    .withDatabaseName("productdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    // 🦜 Kafka container (Confluent image)
    //@Container
    static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    // 🔧 Dynamically inject container properties into Spring Boot
    //@DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL (R2DBC)
        registry.add("spring.r2dbc.url",
                () -> String.format("r2dbc:postgresql://%s:%d/%s",
                        postgres.getHost(), postgres.getFirstMappedPort(), postgres.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);

        // Kafka
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        // Disable migration tools like Flyway/Liquibase if not configured
        registry.add("spring.flyway.enabled", () -> false);
    }
}
