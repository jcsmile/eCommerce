package com.ecommerce.userservice.service;

import com.ecommerce.userservice.domain.User;
import com.ecommerce.userservice.dto.AuthResponse;
import com.ecommerce.userservice.dto.UserDto;
import com.ecommerce.userservice.repo.UserRepository;
import com.ecommerce.userservice.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of UserService using R2DBC repository.
 */
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public UserService(UserRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    public Mono<UserDto> register(UserDto dto) {
        // basic flow: check if username/email exists -> save user -> return dto without password
        return repo.findByUsernameOrEmail(dto.username, dto.email)
                .flatMap(existing -> Mono.<UserDto>error(new IllegalArgumentException("Username or email already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    String hash = passwordEncoder.encode(dto.password);
                    User u = new User(dto.username, dto.email, hash, "USER");
                    return repo.save(u).map(saved -> {
                        UserDto out = new UserDto();
                        out.id = saved.id;
                        out.username = saved.username;
                        out.email = saved.email;
                        out.password = null;
                        return out;
                    });
                }));
    }

    public Mono<AuthResponse> login(String usernameOrEmail, String password) {
        return repo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.passwordHash)) {
                        String token = jwtUtil.generateToken(user.id.toString(), user.username, user.roles);
                        long exp = System.currentTimeMillis() + jwtUtil.getExpirationMs();
                        return Mono.just(new AuthResponse(token, exp));
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid credentials"));
                    }
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
    }

    public Mono<UserDto> getById(Long id) {
        return repo.findById(id).map(u -> {
            UserDto dto = new UserDto();
            dto.id = u.id;
            dto.username = u.username;
            dto.email = u.email;
            dto.password = null;
            return dto;
        });
    }

    public Mono<UserDto> findByUsername(String username) {
        return repo.findByUsername(username).map(u -> {
            UserDto dto = new UserDto();
            dto.id = u.id;
            dto.username = u.username;
            dto.email = u.email;
            dto.password = null;
            return dto;
        });
    }

    public Mono<UserDto> findByEmail(String email) {
        return repo.findByEmail(email).map(u -> {
            UserDto dto = new UserDto();
            dto.id = u.id;
            dto.username = u.username;
            dto.email = u.email;
            dto.password = null;
            return dto;
        });
    }

    public Flux<UserDto> getAllUsers(int page, int size) {
        int skip = page * size;
        return repo.findAll()
                .skip(skip)
                .take(size)
                .map(u -> {;
                    UserDto dto = new UserDto();
                    dto.id = u.id;
                    dto.username = u.username;
                    dto.email = u.email;
                    dto.password = null;
                    return dto;
                });
    }
}
