package com.ecommerce.userservice.dto;

/**
 * Auth response contains JWT token and expiry
 */
public class AuthResponse {
    public String token;
    public long expiresAt;

    public AuthResponse(String token, long expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
