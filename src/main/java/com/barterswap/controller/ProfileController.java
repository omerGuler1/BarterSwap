package com.barterswap.controller;

import com.barterswap.dto.profile.ProfileResponse;
import com.barterswap.dto.profile.UpdateProfileRequest;
import com.barterswap.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        ProfileResponse response = profileService.getProfile();
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.updateProfile(request);
        return ResponseEntity.ok(response);
    }
} 