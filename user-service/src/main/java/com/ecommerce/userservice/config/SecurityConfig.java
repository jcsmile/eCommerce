package com.ecommerce.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Basic reactive security config - permit /api/users/register and /api/users/login for now.
 * JWT filter and RBAC should be implemented and wired here for production.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/users/register", "/users/login", "/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                // Instead of formLogin(), use HTTP Basic or your custom authentication entrypoint
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
