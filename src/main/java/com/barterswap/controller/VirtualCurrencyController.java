package com.barterswap.controller;

import com.barterswap.dto.virtualcurrency.TransactionHistoryResponse;
import com.barterswap.dto.virtualcurrency.VirtualCurrencyBalanceResponse;
import com.barterswap.entity.User;
import com.barterswap.service.VirtualCurrencyService;
import com.barterswap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/virtual-currency")
@RequiredArgsConstructor
public class VirtualCurrencyController {
    private final VirtualCurrencyService virtualCurrencyService;
    private final UserService userService;

    @GetMapping("/balance")
    public ResponseEntity<VirtualCurrencyBalanceResponse> getBalance(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        return ResponseEntity.ok(virtualCurrencyService.getBalanceDetails(user));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        return ResponseEntity.ok(virtualCurrencyService.getTransactionHistory(user));
    }
} 