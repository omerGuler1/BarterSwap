package com.barterswap.repository;

import com.barterswap.entity.Transaction;
import com.barterswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByBuyerOrSeller(User buyer, User seller);
} 