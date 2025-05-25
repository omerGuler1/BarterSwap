package com.barterswap.service;

import com.barterswap.dto.item.CreateItemRequest;
import com.barterswap.dto.item.ItemResponse;
import com.barterswap.dto.item.UpdateItemRequest;
import com.barterswap.dto.item.UpdateItemStatusRequest;
import com.barterswap.entity.Item;
import com.barterswap.entity.ItemImage;
import com.barterswap.entity.User;
import com.barterswap.enums.ItemStatus;
import com.barterswap.exception.ItemException;
import com.barterswap.repository.ItemRepository;
import com.barterswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public ItemResponse createItem(CreateItemRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Creating item for user: {}", username);
            log.info("Request auction end time: {}", request.getAuctionEndTime());
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Create the item
            Item item = Item.builder()
                    .user(user)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .category(request.getCategory())
                    .startingPrice(request.getStartingPrice())
                    .currentPrice(request.getStartingPrice())
                    .condition(request.getCondition())
                    .status(ItemStatus.ACTIVE)
                    .isActive(true)
                    .isDeleted(false)
                    .auctionEndTime(request.getAuctionEndTime())
                    .buyoutPrice(request.getBuyoutPrice())
                    .build();

            log.info("Created item with auction end time: {} and buyout price: {}", item.getAuctionEndTime(), item.getBuyoutPrice());

            log.info("Saving item: {}", item);
            item = itemRepository.save(item);

            // Handle image URLs
            List<String> imageUrls = new ArrayList<>();
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                log.info("Processing {} images", request.getImageUrls().size());
                for (int i = 0; i < request.getImageUrls().size(); i++) {
                    String imageUrl = request.getImageUrls().get(i);
                    imageUrls.add(imageUrl);

                    ItemImage itemImage = ItemImage.builder()
                            .item(item)
                            .imageUrl(imageUrl)
                            .isPrimary(i == 0)
                            .build();

                    log.info("Adding image: {}", imageUrl);
                    item.getImages().add(itemImage);
                }
            }

            log.info("Saving item with images");
            item = itemRepository.save(item);

            return mapToItemResponse(item, imageUrls);
        } catch (Exception e) {
            log.error("Error creating item: {}", e.getMessage(), e);
            throw new ItemException("Failed to create item: " + e.getMessage());
        }
    }

    @Transactional
    public ItemResponse updateItem(Integer itemId, UpdateItemRequest request) {
        log.info("Entering updateItem method with itemId: {} and request: {}", itemId, request);
        try {
            // Validate request
            if (request == null) {
                log.error("Update request is null");
                throw new ItemException("Update request cannot be null");
            }

            if (itemId == null) {
                log.error("Item ID is null");
                throw new ItemException("Item ID cannot be null");
            }

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Starting update for item {} by user: {}", itemId, username);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found: {}", username);
                        return new UsernameNotFoundException("User not found");
                    });
            log.info("Found user: {}", user.getUsername());

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> {
                        log.error("Item not found: {}", itemId);
                        return new ItemException("Item not found");
                    });
            log.info("Found item: {}", item.getTitle());

            // Check if the user is the owner of the item
            if (!item.getUser().getUserId().equals(user.getUserId())) {
                log.error("User {} is not authorized to update item {}", username, itemId);
                throw new ItemException("You are not authorized to update this item");
            }

            log.info("Updating item fields...");
            // Update the item fields
            item.setTitle(request.getTitle());
            item.setDescription(request.getDescription());
            item.setCategory(request.getCategory());
            item.setStartingPrice(request.getStartingPrice());
            item.setCondition(request.getCondition());

            // Handle image URLs
            List<String> imageUrls = new ArrayList<>();
            log.info("Processing image URLs: {}", request.getImageUrls());

            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                log.info("Current images list: {}", item.getImages());

                // Remove images not in the new list
                List<ItemImage> currentImages = new ArrayList<>(item.getImages());
                for (ItemImage img : currentImages) {
                    if (!request.getImageUrls().contains(img.getImageUrl())) {
                        item.getImages().remove(img);
                    }
                }

                // Add new images and update isPrimary
                for (String url : request.getImageUrls()) {
                    imageUrls.add(url);
                    ItemImage img = item.getImages().stream()
                        .filter(i -> i.getImageUrl().equals(url))
                        .findFirst()
                        .orElse(null);

                    if (img == null) {
                        img = ItemImage.builder()
                            .item(item)
                            .imageUrl(url)
                            .isPrimary(url.equals(request.getPrimaryImageUrl()))
                            .build();
                        item.getImages().add(img);
                    } else {
                        img.setIsPrimary(url.equals(request.getPrimaryImageUrl()));
                    }
                }
            }

            log.info("Saving updated item...");
            item = itemRepository.save(item);
            log.info("Item saved successfully");

            return mapToItemResponse(item, imageUrls);
        } catch (Exception e) {
            log.error("Error updating item: {}", e.getMessage(), e);
            throw new ItemException("Failed to update item: " + e.getMessage());
        }
    }

    @Transactional
    public ItemResponse updateItemStatus(Integer itemId, UpdateItemStatusRequest request) {
        log.info("Updating status for item {} to {}", itemId, request.getStatus());
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("User {} is updating item {} status", username, itemId);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found: {}", username);
                        return new UsernameNotFoundException("User not found");
                    });

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> {
                        log.error("Item not found: {}", itemId);
                        return new ItemException("Item not found");
                    });

            // Check if the user is the owner of the item
            if (!item.getUser().getUserId().equals(user.getUserId())) {
                log.error("User {} is not authorized to update item {} status", username, itemId);
                throw new ItemException("You are not authorized to update this item's status");
            }

            // Update the status
            item.setStatus(request.getStatus());
            
            // If status is SOLD or CANCELLED, also set isActive to false
            if (request.getStatus() == ItemStatus.SOLD || request.getStatus() == ItemStatus.CANCELLED) {
                item.setIsActive(false);
            }

            item = itemRepository.save(item);
            log.info("Successfully updated item {} status to {}", itemId, request.getStatus());

            return mapToItemResponse(item, item.getImages().stream()
                    .map(ItemImage::getImageUrl)
                    .toList());
        } catch (Exception e) {
            log.error("Error updating item status: {}", e.getMessage(), e);
            throw new ItemException("Failed to update item status: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getUserItems() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Getting items for user: {}", username);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            List<Item> items = itemRepository.findByUserAndIsDeletedFalse(user);
            log.info("Found {} items for user {}", items.size(), username);

            return items.stream()
                    .map(item -> mapToItemResponse(item, item.getImages().stream()
                            .map(ItemImage::getImageUrl)
                            .toList()))
                    .toList();
        } catch (Exception e) {
            log.error("Error getting user items: {}", e.getMessage(), e);
            throw new ItemException("Failed to get user items: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsByStatus(ItemStatus status) {
        try {
            log.info("Getting items with status: {}", status);
            List<Item> items = itemRepository.findByStatusAndIsDeletedFalse(status);
            log.info("Found {} items with status {}", items.size(), status);

            return items.stream()
                    .map(item -> mapToItemResponse(item, item.getImages().stream()
                            .map(ItemImage::getImageUrl)
                            .toList()))
                    .toList();
        } catch (Exception e) {
            log.error("Error getting items by status: {}", e.getMessage(), e);
            throw new ItemException("Failed to get items by status: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteItem(Integer itemId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("User {} is deleting item {}", username, itemId);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemException("Item not found"));

            // Check if the user is the owner of the item
            if (!item.getUser().getUserId().equals(user.getUserId())) {
                log.error("User {} is not authorized to delete item {}", username, itemId);
                throw new ItemException("You are not authorized to delete this item");
            }

            // Soft delete the item
            item.setIsDeleted(true);
            itemRepository.save(item);
            log.info("Successfully deleted item {}", itemId);
        } catch (Exception e) {
            log.error("Error deleting item: {}", e.getMessage(), e);
            throw new ItemException("Failed to delete item: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getActiveListings() {
        List<Item> items = itemRepository.findByStatusAndIsActiveTrueAndIsDeletedFalse(ItemStatus.ACTIVE);
        return items.stream()
                .map(item -> mapToItemResponse(item, item.getImages().stream().map(ItemImage::getImageUrl).toList()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> searchItems(String keyword, com.barterswap.enums.ItemCategory category) {
        List<Item> items;
        if (keyword != null && !keyword.isBlank() && category != null) {
            items = itemRepository.findByTitleContainingIgnoreCaseAndCategoryAndStatusAndIsActiveTrueAndIsDeletedFalse(keyword, category, ItemStatus.ACTIVE);
        } else if (keyword != null && !keyword.isBlank()) {
            items = itemRepository.findByTitleContainingIgnoreCaseAndStatusAndIsActiveTrueAndIsDeletedFalse(keyword, ItemStatus.ACTIVE);
        } else if (category != null) {
            items = itemRepository.findByCategoryAndStatusAndIsActiveTrueAndIsDeletedFalse(category, ItemStatus.ACTIVE);
        } else {
            items = itemRepository.findByStatusAndIsActiveTrueAndIsDeletedFalse(ItemStatus.ACTIVE);
        }
        return items.stream()
                .map(item -> mapToItemResponse(item, item.getImages().stream().map(ItemImage::getImageUrl).toList()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemDetails(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .filter(i -> !i.getIsDeleted() && i.getIsActive())
                .orElseThrow(() -> new ItemException("Item not found or inactive"));
        return mapToItemResponse(item, item.getImages().stream().map(ItemImage::getImageUrl).toList());
    }

    private ItemResponse mapToItemResponse(Item item, List<String> imageUrls) {
        String primaryImageUrl = item.getImages().stream()
            .filter(ItemImage::getIsPrimary)
            .map(ItemImage::getImageUrl)
            .findFirst()
            .orElseGet(() -> imageUrls.isEmpty() ? null : imageUrls.get(0));

        log.info("Mapping item {} to response, auction end time: {}", item.getItemId(), item.getAuctionEndTime());

        ItemResponse response = ItemResponse.builder()
            .itemId(item.getItemId())
            .title(item.getTitle())
            .description(item.getDescription())
            .category(item.getCategory())
            .startingPrice(item.getStartingPrice())
            .currentPrice(item.getCurrentPrice())
            .condition(item.getCondition())
            .status(item.getStatus())
            .isActive(item.getIsActive())
            .createdAt(item.getCreatedAt())
            .updatedAt(item.getUpdatedAt())
            .imageUrls(imageUrls)
            .primaryImageUrl(primaryImageUrl)
            .sellerUsername(item.getUser().getUsername())
            .sellerId(item.getUser().getUserId())
            .user(ItemResponse.UserSummary.builder()
                .userId(item.getUser().getUserId())
                .username(item.getUser().getUsername())
                .build())
            .auctionEndTime(item.getAuctionEndTime())
            .build();

        log.info("Mapped response auction end time: {}", response.getAuctionEndTime());
        return response;
    }
} 