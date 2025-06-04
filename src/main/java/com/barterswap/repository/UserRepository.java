package com.barterswap.repository;

import com.barterswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByStudentId(String studentId);

    @Query("SELECT DISTINCT u, " +
           "COUNT(t.transactionId) as itemsSold, " +
           "SUM(t.price) as totalIncome, " +
           "COALESCE(AVG(f.score), 0) as avgFeedback " +
           "FROM User u " +
           "JOIN Transaction t ON u = t.seller " +
           "LEFT JOIN Feedback f ON f.transaction = t " +
           "WHERE t.status = 'COMPLETED' " +
           "GROUP BY u " +
           "ORDER BY COUNT(t.transactionId) DESC")
    List<User> findTopSellers();
} 