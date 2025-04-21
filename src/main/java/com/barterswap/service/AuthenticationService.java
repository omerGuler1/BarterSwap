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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VirtualCurrencyRepository virtualCurrencyRepository;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("Email address is already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RegistrationException("Username is already taken");
        }
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new RegistrationException("Student ID is already registered");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .studentId(request.getStudentId())
                .build();

        user = userRepository.save(user);

        var virtualCurrency = VirtualCurrency.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();

        virtualCurrencyRepository.save(virtualCurrency);

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .message("Login successful")
                .build();
    }
} 