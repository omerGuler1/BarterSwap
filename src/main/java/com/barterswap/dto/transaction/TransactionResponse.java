package com.barterswap.dto.transaction;

import com.barterswap.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Integer transactionId;
    private UserSummary buyer;
    private UserSummary seller;
    private ItemSummary item;
    private BigDecimal price;
    private TransactionStatus status;
    private LocalDateTime transactionDate;
    private Boolean hasFeedback;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Integer userId;
        private String username;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemSummary {
        private Integer itemId;
        private String title;
        private String primaryImageUrl;
    }
} 