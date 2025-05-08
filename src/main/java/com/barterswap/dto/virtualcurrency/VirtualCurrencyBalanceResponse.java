package com.barterswap.dto.virtualcurrency;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class VirtualCurrencyBalanceResponse {
    private BigDecimal balance;
    private String username;
    private Integer userId;
    private String lastUpdated;
} 