package com.barterswap.service;

import com.barterswap.dto.auth.AuthResponse;
import com.barterswap.dto.auth.LoginRequest;
import com.barterswap.dto.auth.RegisterRequest;
import com.barterswap.entity.User;
import com.barterswap.entity.VirtualCurrency;
import com.barterswap.exception.AuthenticationException;
import com.barterswap.exception.RegistrationException;
import com.barterswap.repository.UserRepository;
import com.barterswap.repository.VirtualCurrencyRepository;
import com.barterswap.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private VirtualCurrencyRepository virtualCurrencyRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private VirtualCurrency virtualCurrency;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .studentId("12345")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .userId(1)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .studentId("12345")
                .build();

        virtualCurrency = VirtualCurrency.builder()
                .user(user)
                .balance(java.math.BigDecimal.ZERO)
                .build();
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenRegistrationSuccessful() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByStudentId(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(virtualCurrencyRepository.save(any(VirtualCurrency.class))).thenReturn(virtualCurrency);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthResponse response = authenticationService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("User registered successfully", response.getMessage());
    }

    @Test
    void register_ShouldThrowRegistrationException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> authenticationService.register(registerRequest));
        assertEquals("Email address is already registered", exception.getMessage());
    }

    @Test
    void register_ShouldThrowRegistrationException_WhenUsernameExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> authenticationService.register(registerRequest));
        assertEquals("Username is already taken", exception.getMessage());
    }

    @Test
    void register_ShouldThrowRegistrationException_WhenStudentIdExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByStudentId(anyString())).thenReturn(true);

        // Act & Assert
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> authenticationService.register(registerRequest));
        assertEquals("Student ID is already registered", exception.getMessage());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenLoginSuccessful() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthResponse response = authenticationService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_ShouldThrowAuthenticationException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authenticationService.login(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void login_ShouldThrowAuthenticationException_WhenPasswordInvalid() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authenticationService.login(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }
} 