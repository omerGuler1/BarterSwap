package com.barterswap.dto.item;

import com.barterswap.enums.ItemCategory;
import com.barterswap.enums.ItemCondition;
import com.barterswap.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Integer itemId;
    private String title;
    private String description;
    private ItemCategory category;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private ItemCondition condition;
    private ItemStatus status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> imageUrls;
    private String sellerUsername;
    private Integer sellerId;
    private String primaryImageUrl;
    private LocalDateTime auctionEndTime;
    private UserSummary user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Integer userId;
        private String username;
    }
} 