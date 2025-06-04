package com.barterswap.service;

import com.barterswap.dto.transaction.TransactionResponse;
import com.barterswap.entity.Transaction;
import com.barterswap.entity.User;
import com.barterswap.entity.Item;
import com.barterswap.enums.TransactionStatus;
import com.barterswap.repository.TransactionRepository;
import com.barterswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> getMyPurchases() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Transaction> transactions = transactionRepository.findByBuyerAndStatusNot(user, TransactionStatus.CANCELLED);
        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getMySales() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Transaction> transactions = transactionRepository.findBySellerAndStatusNot(user, TransactionStatus.CANCELLED);
        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .buyer(mapToUserSummary(transaction.getBuyer()))
                .seller(mapToUserSummary(transaction.getSeller()))
                .item(mapToItemSummary(transaction.getItem()))
                .price(transaction.getPrice())
                .status(transaction.getStatus())
                .transactionDate(transaction.getTransactionDate())
                .hasFeedback(transaction.getFeedback() != null)
                .build();
    }

    private TransactionResponse.UserSummary mapToUserSummary(User user) {
        return TransactionResponse.UserSummary.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
    }

    private TransactionResponse.ItemSummary mapToItemSummary(Item item) {
        return TransactionResponse.ItemSummary.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .primaryImageUrl(item.getImages().isEmpty() ? null : item.getImages().get(0).getImageUrl())
                .build();
    }
} 