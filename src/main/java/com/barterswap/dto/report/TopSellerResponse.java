package com.barterswap.dto.report;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TopSellerResponse {
    private Integer userId;
    private String username;
    private Long itemsSold;
    private BigDecimal totalIncome;
    private Double averageFeedbackScore;
    private Integer totalFeedbacks;
    private String reputation; // e.g., "Excellent", "Good", "Average"
} 