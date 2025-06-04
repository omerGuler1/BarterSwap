package com.barterswap.repository;

import com.barterswap.entity.Item;
import com.barterswap.entity.Transaction;
import com.barterswap.entity.User;
import com.barterswap.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByBuyerOrSeller(User buyer, User seller);
    
    // Find completed transactions by buyer (for feedback purposes)
    List<Transaction> findByBuyerAndStatus(User buyer, TransactionStatus status);
    
    // Find completed transactions by seller
    List<Transaction> findBySellerAndStatus(User seller, TransactionStatus status);

    // Find all transactions for a buyer that aren't cancelled
    List<Transaction> findByBuyerAndStatusNot(User buyer, TransactionStatus status);
    
    // Find all transactions for a seller that aren't cancelled
    List<Transaction> findBySellerAndStatusNot(User seller, TransactionStatus status);

    Optional<Transaction> findByItem(Item item);
} 