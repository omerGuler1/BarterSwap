package com.barterswap.controller;

import com.barterswap.dto.report.CategoryReportResponse;
import com.barterswap.dto.report.TopSellerResponse;
import com.barterswap.dto.report.MostBiddedItemResponse;
import com.barterswap.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportingController {
    private final ReportingService reportingService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryReportResponse>> getCategoryReport() {
        return ResponseEntity.ok(reportingService.generateCategoryReport());
    }

    @GetMapping("/top-sellers")
    public ResponseEntity<List<TopSellerResponse>> getTopSellers() {
        return ResponseEntity.ok(reportingService.generateTopSellersReport());
    }

    @GetMapping("/most-bidded")
    public ResponseEntity<List<MostBiddedItemResponse>> getMostBiddedItems() {
        return ResponseEntity.ok(reportingService.getMostBiddedItems());
    }
} 