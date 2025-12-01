package com.ecommerce.productservice.config;

import com.ecommerce.productservice.event.ProductStockEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Kafka settings in the Product Service.
 * Provides consumer factory and listener container factory for ProductStockEvent.
 */
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    /**
     * Consumer Factory for ProductStockEvent
     */
    @Bean
    public ConsumerFactory<String, ProductStockEvent> productStockEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Use Spring Boot properties automatically via application.properties / application.yml
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);

        // Configure ErrorHandlingDeserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Set delegate classes
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Allow JsonDeserializer to deserialize ProductStockEvent
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ProductStockEvent.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ecommerce.productservice.event");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Kafka Listener Container Factory for ProductStockEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductStockEvent> productStockEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductStockEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productStockEventConsumerFactory());
        return factory;
    }
}
