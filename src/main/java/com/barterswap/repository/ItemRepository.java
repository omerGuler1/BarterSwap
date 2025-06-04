package com.barterswap.repository;

import com.barterswap.entity.Item;
import com.barterswap.entity.User;
import com.barterswap.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    List<Item> findByStatusAndAuctionEndTimeBefore(ItemStatus status, LocalDateTime endTime);

    @Query("SELECT DISTINCT i, COUNT(b) as bidCount FROM Item i " +
           "LEFT JOIN i.bids b " +
           "JOIN i.user u " +
           "LEFT JOIN i.images img " +
           "WHERE i.status = :status " +
           "AND i.isActive = true " +
           "AND i.isDeleted = false " +
           "GROUP BY i " +
           "HAVING COUNT(b) > 0 " +
           "ORDER BY COUNT(b) DESC")
    List<Item> findMostBiddedActiveItems(ItemStatus status);

    @Query(value = 
        "SELECT " +
        "   i.category, " +
        "   COUNT(CASE WHEN i.status = 'ACTIVE' AND i.is_active = true AND i.is_deleted = false THEN 1 END) as active_items, " +
        "   COUNT(CASE WHEN i.status = 'SOLD' THEN 1 END) as sold_items, " +
        "   COUNT(*) as total_items, " +
        "   COALESCE(AVG(i.starting_price), 0) as avg_starting_price, " +
        "   COALESCE(AVG(CASE WHEN i.status = 'SOLD' THEN t.price END), 0) as avg_sold_price, " +
        "   CASE " +
        "       WHEN COUNT(*) > 0 THEN " +
        "           ROUND(CAST(COUNT(CASE WHEN i.status = 'SOLD' THEN 1 END) AS DECIMAL) * 100.0 / COUNT(*), 2) " +
        "       ELSE 0 " +
        "   END as conversion_rate " +
        "FROM item i " +
        "LEFT JOIN transaction t ON t.item_id = i.item_id " +
        "WHERE i.is_deleted = false " +
        "GROUP BY i.category " +
        "ORDER BY sold_items DESC", 
    nativeQuery = true)
    List<Object[]> getCategoryPerformanceStats();
} 