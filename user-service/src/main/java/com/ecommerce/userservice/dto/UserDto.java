package com.ecommerce.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Public DTO used for registration and profile responses.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    public Long id;

    @NotBlank
    public String username;

    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String password;

}
