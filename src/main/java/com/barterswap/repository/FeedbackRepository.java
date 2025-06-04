package com.barterswap.repository;

import com.barterswap.entity.Feedback;
import com.barterswap.entity.Transaction;
import com.barterswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    
    // Find feedback by transaction (to check if feedback already exists)
    Optional<Feedback> findByTransaction(Transaction transaction);
    
    // Find all feedback received by a user (seller)
    List<Feedback> findByReceiver(User receiver);
    
    // Find all feedback given by a user (buyer)
    List<Feedback> findByGiver(User giver);
    
    // Count total feedback for a user
    Long countByReceiver(User receiver);
    
    // Calculate average rating for a user
    @Query("SELECT CAST(AVG(CAST(f.score as int)) as double) FROM Feedback f WHERE f.receiver = :user")
    Double calculateAverageRating(@Param("user") User user);
    
    // Count feedback by score for a user
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.receiver = :user AND f.score = :score")
    Integer countByReceiverAndScore(@Param("user") User user, @Param("score") com.barterswap.enums.FeedbackScore score);
    
    // Check if feedback exists for a transaction
    boolean existsByTransaction(Transaction transaction);
} 