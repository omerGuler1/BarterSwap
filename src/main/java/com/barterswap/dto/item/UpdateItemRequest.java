package com.barterswap.dto.item;

import com.barterswap.enums.ItemCategory;
import com.barterswap.enums.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Category is required")
    private ItemCategory category;

    @NotNull(message = "Starting price is required")
    @Positive(message = "Starting price must be positive")
    private BigDecimal startingPrice;

    @NotNull(message = "Condition is required")
    private ItemCondition condition;

    private List<String> imageUrls;
} 