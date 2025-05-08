package com.barterswap.repository;

import com.barterswap.entity.Item;
import com.barterswap.entity.User;
import com.barterswap.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByUserAndIsDeletedFalse(User user);
    List<Item> findByStatusAndIsDeletedFalse(ItemStatus status);
    List<Item> findByStatusAndIsActiveTrueAndIsDeletedFalse(ItemStatus status);
    List<Item> findByCategoryAndStatusAndIsActiveTrueAndIsDeletedFalse(com.barterswap.enums.ItemCategory category, ItemStatus status);
    List<Item> findByTitleContainingIgnoreCaseAndStatusAndIsActiveTrueAndIsDeletedFalse(String keyword, ItemStatus status);
    List<Item> findByTitleContainingIgnoreCaseAndCategoryAndStatusAndIsActiveTrueAndIsDeletedFalse(String keyword, com.barterswap.enums.ItemCategory category, ItemStatus status);
    List<Item> findByAuctionEndTimeBeforeAndStatusAndIsActiveTrue(LocalDateTime time, ItemStatus status);
} 