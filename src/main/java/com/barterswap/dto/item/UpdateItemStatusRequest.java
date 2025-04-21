package com.barterswap.dto.item;

import com.barterswap.enums.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemStatusRequest {
    @NotNull(message = "Status is required")
    private ItemStatus status;
} 