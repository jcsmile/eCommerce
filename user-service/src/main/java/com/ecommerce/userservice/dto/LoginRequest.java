package com.ecommerce.userservice.dto;

import javax.validation.constraints.NotBlank;

/**
 * Login request payload
 */
public class LoginRequest {
    @NotBlank
    public String usernameOrEmail;
    @NotBlank
    public String password;
}
