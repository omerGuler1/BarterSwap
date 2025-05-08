package com.barterswap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "virtual-currency")
@Data
public class VirtualCurrencyConfig {
    private BigDecimal startingBalance = new BigDecimal("1000.00");
    private BigDecimal positiveFeedbackReward = new BigDecimal("50.00");
    private BigDecimal negativeFeedbackPenalty = new BigDecimal("-25.00");
    private BigDecimal taskCompletionReward = new BigDecimal("100.00");
} 