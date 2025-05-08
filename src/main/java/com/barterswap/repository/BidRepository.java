package com.barterswap.repository;

import com.barterswap.entity.Bid;
import com.barterswap.entity.Item;
import com.barterswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    List<Bid> findByItemOrderByBidAmountDesc(Item item);
    List<Bid> findByUser(User user);
    List<Bid> findByItem(Item item);
} 