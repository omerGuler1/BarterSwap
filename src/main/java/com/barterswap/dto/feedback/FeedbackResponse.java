package com.barterswap.dto.feedback;

import com.barterswap.enums.FeedbackScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private Integer feedbackId;
    private Integer giverId;
    private String giverUsername;
    private Integer receiverId;
    private String receiverUsername;
    private Integer transactionId;
    private Integer itemId;
    private String itemTitle;
    private FeedbackScore score;
    private String comment;
    private LocalDateTime timestamp;
} 