package com.barterswap.repository;

import com.barterswap.entity.User;
import com.barterswap.entity.VirtualCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VirtualCurrencyRepository extends JpaRepository<VirtualCurrency, Integer> {
    Optional<VirtualCurrency> findByUser(User user);
} 