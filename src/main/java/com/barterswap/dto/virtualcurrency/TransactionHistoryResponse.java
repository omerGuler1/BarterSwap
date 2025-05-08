package com.barterswap.dto.virtualcurrency;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionHistoryResponse {
    private Long transactionId;
    private String type; // BID, FEEDBACK_REWARD, TASK_REWARD, etc.
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private String relatedItemTitle; // Optional, for bid-related transactions
    private Integer sellerId;
    private Integer buyerId;
} 