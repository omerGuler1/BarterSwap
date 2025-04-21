package com.barterswap.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Integer userId;
    private String username;
    private String email;
    private String studentId;
    private Integer reputation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 