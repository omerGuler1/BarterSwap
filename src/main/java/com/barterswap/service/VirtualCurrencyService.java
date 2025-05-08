package com.barterswap.service;

import com.barterswap.config.VirtualCurrencyConfig;
import com.barterswap.dto.virtualcurrency.TransactionHistoryResponse;
import com.barterswap.dto.virtualcurrency.VirtualCurrencyBalanceResponse;
import com.barterswap.entity.Transaction;
import com.barterswap.entity.User;
import com.barterswap.entity.VirtualCurrency;
import com.barterswap.exception.VirtualCurrencyException;
import com.barterswap.repository.VirtualCurrencyRepository;
import com.barterswap.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VirtualCurrencyService {
    private final VirtualCurrencyRepository virtualCurrencyRepository;
    private final VirtualCurrencyConfig config;
    private final TransactionRepository transactionRepository;

    @Transactional
    public VirtualCurrency createInitialBalance(User user) {
        VirtualCurrency virtualCurrency = VirtualCurrency.builder()
                .user(user)
                .balance(config.getStartingBalance())
                .build();
        return virtualCurrencyRepository.save(virtualCurrency);
    }

    @Transactional
    public void addPositiveFeedbackReward(User user) {
        VirtualCurrency virtualCurrency = virtualCurrencyRepository.findByUser(user)
                .orElseThrow(() -> new VirtualCurrencyException("Virtual currency not found for user"));
        virtualCurrency.setBalance(virtualCurrency.getBalance().add(config.getPositiveFeedbackReward()));
        virtualCurrencyRepository.save(virtualCurrency);
    }

    @Transactional
    public void addNegativeFeedbackPenalty(User user) {
        VirtualCurrency virtualCurrency = virtualCurrencyRepository.findByUser(user)
                .orElseThrow(() -> new VirtualCurrencyException("Virtual currency not found for user"));
        virtualCurrency.setBalance(virtualCurrency.getBalance().add(config.getNegativeFeedbackPenalty()));
        virtualCurrencyRepository.save(virtualCurrency);
    }

    @Transactional
    public void addTaskCompletionReward(User user) {
        VirtualCurrency virtualCurrency = virtualCurrencyRepository.findByUser(user)
                .orElseThrow(() -> new VirtualCurrencyException("Virtual currency not found for user"));
        virtualCurrency.setBalance(virtualCurrency.getBalance().add(config.getTaskCompletionReward()));
        virtualCurrencyRepository.save(virtualCurrency);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(User user) {
        return virtualCurrencyRepository.findByUser(user)
                .map(VirtualCurrency::getBalance)
                .orElseThrow(() -> new VirtualCurrencyException("Virtual currency not found for user"));
    }

    @Transactional(readOnly = true)
    public VirtualCurrencyBalanceResponse getBalanceDetails(User user) {
        VirtualCurrency virtualCurrency = virtualCurrencyRepository.findByUser(user)
                .orElseThrow(() -> new VirtualCurrencyException("Virtual currency not found for user"));
        
        return VirtualCurrencyBalanceResponse.builder()
                .balance(virtualCurrency.getBalance())
                .username(user.getUsername())
                .userId(user.getUserId())
                .lastUpdated(virtualCurrency.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getTransactionHistory(User user) {
        List<Transaction> transactions = transactionRepository.findByBuyerOrSeller(user, user);
        return transactions.stream()
                .map(transaction -> TransactionHistoryResponse.builder()
                        .transactionId(transaction.getTransactionId().longValue())
                        .type(transaction.getStatus().name())
                        .amount(transaction.getPrice())
                        .description("Transaction for " + transaction.getItem().getTitle())
                        .timestamp(transaction.getTransactionDate())
                        .relatedItemTitle(transaction.getItem().getTitle())
                        .sellerId(transaction.getSeller().getUserId())
                        .buyerId(transaction.getBuyer().getUserId())
                        .build())
                .collect(Collectors.toList());
    }
} 