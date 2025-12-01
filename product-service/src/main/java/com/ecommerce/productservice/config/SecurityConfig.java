package com.ecommerce.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration class for Spring Security settings in the Product Service.
 * <p>
 * Business rules:
 * 1. Enables reactive method security
 * 2. Configures HTTP security for endpoints
 * 3. Permits access to actuator and API docs
 *
 * @author JackyChen
 * @since 2025-04-01
 */
@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    /**
     * Configures the security web filter chain for the application.
     * <p>
     * Business rules:
     * 1. Permits access to actuator and API docs endpoints
     * 2. Requires authentication for other exchanges
     * 3. Enables HTTP basic authentication
     *
     * @param http the ServerHttpSecurity to configure
     * @return the configured SecurityWebFilterChain
     * @author JackyChen
     * @since 2025-04-01
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .pathMatchers("/api/products/init").permitAll() //hasRole("ADMIN")
                        .pathMatchers("/api/products/**").permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
