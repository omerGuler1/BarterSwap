package com.barterswap.service;

import com.barterswap.entity.User;
import com.barterswap.exception.AuthenticationException;
import com.barterswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }
} 