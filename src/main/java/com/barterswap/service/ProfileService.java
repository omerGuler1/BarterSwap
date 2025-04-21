package com.barterswap.service;

import com.barterswap.dto.profile.ProfileResponse;
import com.barterswap.dto.profile.UpdateProfileRequest;
import com.barterswap.entity.User;
import com.barterswap.exception.ValidationException;
import com.barterswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.FieldError;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final Validator validator;

    public ProfileResponse getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ProfileResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .reputation(user.getReputation())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Validate the request
        Errors errors = new BeanPropertyBindingResult(request, "updateProfileRequest");
        validator.validate(request, errors);

        if (errors.hasErrors()) {
            throw new ValidationException("Validation failed", errors.getFieldErrors());
        }

        // Check for duplicate username if changed
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Validation failed", List.of(
                new FieldError("updateProfileRequest", "username", "Username is already taken")
            ));
        }

        // Check for duplicate email if changed
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Validation failed", List.of(
                new FieldError("updateProfileRequest", "email", "Email is already taken")
            ));
        }

        // Check for duplicate student ID if changed
        if (!user.getStudentId().equals(request.getStudentId()) && 
            userRepository.existsByStudentId(request.getStudentId())) {
            throw new ValidationException("Validation failed", List.of(
                new FieldError("updateProfileRequest", "studentId", "Student ID is already taken")
            ));
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setStudentId(request.getStudentId());
        user = userRepository.save(user);

        return ProfileResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .reputation(user.getReputation())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
} 