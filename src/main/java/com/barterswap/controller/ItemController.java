package com.barterswap.controller;

import com.barterswap.dto.item.CreateItemRequest;
import com.barterswap.dto.item.ItemResponse;
import com.barterswap.dto.item.UpdateItemRequest;
import com.barterswap.dto.item.UpdateItemStatusRequest;
import com.barterswap.enums.ItemStatus;
import com.barterswap.exception.ItemException;
import com.barterswap.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
        ItemResponse response = itemService.createItem(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Integer itemId,
            @Valid @RequestBody UpdateItemRequest request) {
        log.info("Received update request for item {}: {}", itemId, request);
        try {
            ItemResponse response = itemService.updateItem(itemId, request);
            log.info("Successfully updated item {}", itemId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in updateItem controller: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{itemId}/status")
    public ResponseEntity<?> updateItemStatus(
            @PathVariable Integer itemId,
            @RequestBody UpdateItemStatusRequest request) {
        log.info("Received request to update status for item {} to {}", itemId, request.getStatus());
        try {
            ItemResponse response = itemService.updateItemStatus(itemId, request);
            log.info("Successfully updated status for item {}", itemId);
            return ResponseEntity.ok(response);
        } catch (ItemException e) {
            log.error("Error updating item status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to update item status",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Unexpected error updating item status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Internal server error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<ItemResponse>> getUserItems() {
        log.info("Received request to get user's items");
        try {
            List<ItemResponse> items = itemService.getUserItems();
            log.info("Successfully retrieved {} items for user", items.size());
            return ResponseEntity.ok(items);
        } catch (ItemException e) {
            log.error("Error getting user items: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ItemResponse>> getItemsByStatus(@PathVariable ItemStatus status) {
        log.info("Received request to get items with status: {}", status);
        try {
            List<ItemResponse> items = itemService.getItemsByStatus(status);
            log.info("Successfully retrieved {} items with status {}", items.size(), status);
            return ResponseEntity.ok(items);
        } catch (ItemException e) {
            log.error("Error getting items by status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer itemId) {
        log.info("Received request to delete item: {}", itemId);
        try {
            itemService.deleteItem(itemId);
            log.info("Successfully deleted item: {}", itemId);
            return ResponseEntity.ok().build();
        } catch (ItemException e) {
            log.error("Error deleting item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
} 