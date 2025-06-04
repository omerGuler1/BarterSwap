package com.barterswap.dto.report;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CategoryReportResponse {
    private String category;
    private Long activeListings;
    private Long itemsSold;
    private Long totalItems;
    private BigDecimal averageStartingPrice;
    private BigDecimal averageSoldPrice;
    private BigDecimal conversionRate; // Percentage of items sold
} 