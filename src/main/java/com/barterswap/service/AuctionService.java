package com.barterswap.service;

import com.barterswap.entity.Item;
import com.barterswap.enums.ItemStatus;
import com.barterswap.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {
    private final ItemRepository itemRepository;

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void checkExpiredAuctions() {
        log.info("Checking for expired auctions...");
        LocalDateTime now = LocalDateTime.now();
        
        List<Item> expiredItems = itemRepository.findByAuctionEndTimeBeforeAndStatusAndIsActiveTrue(now, ItemStatus.ACTIVE);
        
        for (Item item : expiredItems) {
            log.info("Processing expired auction for item: {}", item.getItemId());
            
            // If there are no bids, mark as CANCELLED
            if (item.getBids().isEmpty()) {
                item.setStatus(ItemStatus.CANCELLED);
                log.info("No bids found, marking item {} as CANCELLED", item.getItemId());
            } else {
                // If there are bids, mark as SOLD
                item.setStatus(ItemStatus.SOLD);
                log.info("Bids found, marking item {} as SOLD", item.getItemId());
            }
            
            item.setIsActive(false);
            itemRepository.save(item);
        }
        
        log.info("Finished processing expired auctions. Found {} expired items", expiredItems.size());
    }
} 