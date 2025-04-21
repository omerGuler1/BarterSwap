package com.barterswap.repository;

import com.barterswap.entity.VirtualCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualCurrencyRepository extends JpaRepository<VirtualCurrency, Integer> {
} 