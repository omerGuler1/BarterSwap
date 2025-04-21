package com.barterswap.service;

import com.barterswap.dto.profile.ProfileResponse;
import com.barterswap.dto.profile.UpdateProfileRequest;
import com.barterswap.entity.User;
import com.barterswap.exception.ValidationException;
import com.barterswap.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProfileService profileService;

    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        profileService = new ProfileService(userRepository, validator);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getProfile_ShouldReturnProfile_WhenUserExists() {
        // Arrange
        User user = createTestUser();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        ProfileResponse response = profileService.getProfile();

        // Assert
        assertNotNull(response);
        assertEquals(user.getUserId(), response.getUserId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getStudentId(), response.getStudentId());
        assertEquals(user.getReputation(), response.getReputation());
    }

    @Test
    void getProfile_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> profileService.getProfile());
    }

    @Test
    void updateProfile_ShouldUpdateProfile_WhenNewValuesAreUnique() {
        // Arrange
        User user = createTestUser();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .username("newusername")
                .email("newemail@example.com")
                .studentId("newstudentid")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.existsByStudentId("newstudentid")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        ProfileResponse response = profileService.updateProfile(request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getUsername(), response.getUsername());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getStudentId(), response.getStudentId());
    }

    @Test
    void updateProfile_ShouldThrowValidationException_WhenUsernameExists() {
        // Arrange
        User user = createTestUser();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .username("existingusername")
                .email("newemail@example.com")
                .studentId("newstudentid")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existingusername")).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> profileService.updateProfile(request));
    }

    @Test
    void updateProfile_ShouldThrowValidationException_WhenEmailExists() {
        // Arrange
        User user = createTestUser();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .username("newusername")
                .email("existing@example.com")
                .studentId("newstudentid")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> profileService.updateProfile(request));
    }

    @Test
    void updateProfile_ShouldThrowValidationException_WhenStudentIdExists() {
        // Arrange
        User user = createTestUser();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .username("newusername")
                .email("newemail@example.com")
                .studentId("existingstudentid")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.existsByStudentId("existingstudentid")).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> profileService.updateProfile(request));
    }

    private User createTestUser() {
        return User.builder()
                .userId(1)
                .username("testuser")
                .email("test@example.com")
                .studentId("123456")
                .reputation(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
} 