package com.barterswap.dto.feedback;

import com.barterswap.enums.FeedbackScore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedbackRequest {
    
    @NotNull(message = "Transaction ID is required")
    private Integer transactionId;
    
    @NotNull(message = "Feedback score is required")
    private FeedbackScore score;
    
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;
} 