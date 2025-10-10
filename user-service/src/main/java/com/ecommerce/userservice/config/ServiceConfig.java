package com.ecommerce.userservice.config;

import com.ecommerce.userservice.repo.UserRepository;
import com.ecommerce.userservice.service.UserService;
import com.ecommerce.userservice.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Manual bean wiring for service implementation to allow easy unit testing.
 */
@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService(UserRepository userRepository, JwtUtil jwtUtil) {
        return new UserService(userRepository, jwtUtil);
    }
}
