package com.barterswap.dto.bid;

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
public class BidResponse {
    private Integer bidId;
    private Integer itemId;
    private Integer userId;
    private BigDecimal bidAmount;
    private LocalDateTime timestamp;
} 