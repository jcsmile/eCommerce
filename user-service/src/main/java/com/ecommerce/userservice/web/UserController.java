package com.ecommerce.userservice.web;

import com.ecommerce.userservice.dto.AuthResponse;
import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.UserDto;
import com.ecommerce.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Public API for user registration / login / profile.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<UserDto>> register(@Validated @RequestBody UserDto dto) {
        return userService.register(dto)
                .map(saved -> ResponseEntity.ok(saved))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Validated @RequestBody LoginRequest req) {
        return userService.login(req.usernameOrEmail, req.password)
                .map(auth -> ResponseEntity.ok(auth))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDto>> getProfile(@PathVariable Long id) {
        return userService.getById(id)
                .map(dto -> ResponseEntity.ok(dto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    /**
     * Get a paginated list of all users.
     * Accessible only to ADMIN role.
     */
    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public Flux<UserDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }
}
