package com.ecommerce.userservice.service;

import com.ecommerce.userservice.domain.User;
import com.ecommerce.userservice.dto.UserDto;
import com.ecommerce.userservice.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_NewUser_Success() {
        String email = "new@example.com";
        String username = "newuser";
        String password = "secret";

        User savedUser = new User(username, email, "encoded-secret", "USER");

        when(userRepository.findByEmail(eq(email))).thenReturn(Mono.empty());
        when(passwordEncoder.encode(eq(password))).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Mono.empty());


        UserDto dto = new UserDto();
        dto.email = email;
        dto.username = username;
        dto.password = password;

        StepVerifier.create(userService.register(dto))
                .expectNextMatches(user -> user.email.equals(email)
                        && user.username.equals(username)
                        && user.password == null)
                .verifyComplete();

        verify(userRepository).save(any(User.class));
        verify(userRepository).findByUsernameOrEmail(eq(username),eq(email));
    }

    @Test
    void register_ExistingEmail_Fails() {
        String email = "existing@example.com";
        String username = "user";
        String password = "pwd";

        User existingUser = new User();
        existingUser.email = email;

        when(userRepository.findByEmail(eq(email))).thenReturn(Mono.just(existingUser));
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Mono.just(existingUser));

        UserDto dto = new UserDto();
        dto.email = email;
        dto.username = username;
        dto.password = password;
        StepVerifier.create(userService.register(dto))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().equals("Username or email already exists"))
                .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_ReturnsUser() {
        String email = "user@example.com";
        User user = new User();
        user.email = email;
        user.username = "tester";

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.findByEmail(email))
                .expectNextMatches(found -> found.email.equals(email))
                .verifyComplete();
    }
}
