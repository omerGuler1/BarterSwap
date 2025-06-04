package com.barterswap.controller;

import com.barterswap.dto.transaction.TransactionResponse;
import com.barterswap.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/my-purchases")
    public ResponseEntity<List<TransactionResponse>> getMyPurchases() {
        return ResponseEntity.ok(transactionService.getMyPurchases());
    }

    @GetMapping("/my-sales")
    public ResponseEntity<List<TransactionResponse>> getMySales() {
        return ResponseEntity.ok(transactionService.getMySales());
    }
} 