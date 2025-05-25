package com.barterswap.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.barterswap.dto.RegisterRequest;
import com.barterswap.dto.ErrorResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Your existing registration logic
            // For example:
            // userService.register(request);
            return ResponseEntity.ok("Registration successful");
        } catch (Exception e) {
            // Log the full exception
            e.printStackTrace(); // or use a logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }
} 