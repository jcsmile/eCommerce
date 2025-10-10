package com.ecommerce.userservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * User domain entity stored in Postgres via R2DBC.
 */
@Table("users")
@Data
public class User {
    @Id
    public Long id;
    public String username;
    public String email;
    public String passwordHash;
    public String roles; // comma-separated roles

    public User() {}

    public User(String username, String email, String passwordHash, String roles) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }
}
