package com.barterswap.controller;

import com.barterswap.dto.bid.BidResponse;
import com.barterswap.dto.bid.PlaceBidRequest;
import com.barterswap.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(@Valid @RequestBody PlaceBidRequest request) {
        BidResponse response = bidService.placeBid(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/highest")
    public ResponseEntity<BidResponse> getHighestBid(@RequestParam Integer itemId) {
        BidResponse response = bidService.getHighestBid(itemId);
        return ResponseEntity.ok(response);
    }
} 