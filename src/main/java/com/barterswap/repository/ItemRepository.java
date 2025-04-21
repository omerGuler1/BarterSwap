package com.barterswap.repository;

import com.barterswap.entity.Item;
import com.barterswap.entity.User;
import com.barterswap.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByUserAndIsDeletedFalse(User user);
    List<Item> findByStatusAndIsDeletedFalse(ItemStatus status);
} 