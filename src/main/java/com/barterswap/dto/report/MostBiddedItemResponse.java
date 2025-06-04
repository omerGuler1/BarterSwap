package com.barterswap.dto.report;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MostBiddedItemResponse {
    private Integer itemId;
    private String itemTitle;
    private Integer numberOfBids;
    private BigDecimal finalPrice;
    private String sellerUsername;
    private String status;
    private String primaryImageUrl;
    private LocalDateTime auctionEndTime;
} 