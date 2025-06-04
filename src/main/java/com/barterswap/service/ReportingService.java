package com.barterswap.service;

import com.barterswap.dto.report.CategoryReportResponse;
import com.barterswap.dto.report.TopSellerResponse;
import com.barterswap.dto.report.MostBiddedItemResponse;
import com.barterswap.entity.Item;
import com.barterswap.entity.Transaction;
import com.barterswap.entity.User;
import com.barterswap.enums.ItemStatus;
import com.barterswap.enums.TransactionStatus;
import com.barterswap.repository.ItemRepository;
import com.barterswap.repository.TransactionRepository;
import com.barterswap.repository.UserRepository;
import com.barterswap.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportingService {
    private final ItemRepository itemRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional(readOnly = true)
    public List<CategoryReportResponse> generateCategoryReport() {
        try {
            log.debug("Fetching category performance stats");
            List<Object[]> results = itemRepository.getCategoryPerformanceStats();
            log.debug("Found {} category records", results.size());

            return results.stream()
                .map(row -> {
                    try {
                        String category = row[0] != null ? row[0].toString() : "Uncategorized";
                        log.trace("Processing category: {}", category);

                        return CategoryReportResponse.builder()
                            .category(category)
                            .activeListings(convertToLong(row[1]))
                            .itemsSold(convertToLong(row[2]))
                            .totalItems(convertToLong(row[3]))
                            .averageStartingPrice(convertToBigDecimal(row[4]))
                            .averageSoldPrice(convertToBigDecimal(row[5]))
                            .conversionRate(convertToBigDecimal(row[6]))
                            .build();
                    } catch (Exception e) {
                        log.error("Error processing category row: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error generating category report: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private Long convertToLong(Object value) {
        if (value == null) return 0L;
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return ((BigDecimal) value).setScale(2, RoundingMode.HALF_UP);
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue()).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<TopSellerResponse> generateTopSellersReport() {
        return userRepository.findTopSellers()
            .stream()
            .limit(10)
            .map(user -> {
                // Get completed transactions where user is seller
                List<Transaction> completedSales = transactionRepository
                        .findBySellerAndStatus(user, TransactionStatus.COMPLETED);
                
                if (completedSales.isEmpty()) {
                    return null; // Skip users with no sales
                }
                
                // Calculate total income
                BigDecimal totalIncome = completedSales.stream()
                        .map(Transaction::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // Calculate average feedback score
                Double avgFeedback = feedbackRepository.calculateAverageRating(user);
                Integer totalFeedbacks = feedbackRepository.countByReceiver(user).intValue();
                
                // Determine reputation level based on average feedback
                String reputation = determineReputation(avgFeedback);
                
                return TopSellerResponse.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .itemsSold((long) completedSales.size())
                        .totalIncome(totalIncome)
                        .averageFeedbackScore(avgFeedback)
                        .totalFeedbacks(totalFeedbacks)
                        .reputation(reputation)
                        .build();
            })
            .filter(response -> response != null)
            .collect(Collectors.toList());
    }

    private String determineReputation(Double avgFeedback) {
        if (avgFeedback == null) return "New Seller";
        if (avgFeedback >= 4.5) return "Excellent";
        if (avgFeedback >= 4.0) return "Very Good";
        if (avgFeedback >= 3.5) return "Good";
        if (avgFeedback >= 3.0) return "Average";
        return "Below Average";
    }

    @Transactional(readOnly = true)
    public List<MostBiddedItemResponse> getMostBiddedItems() {
        return itemRepository.findMostBiddedActiveItems(ItemStatus.ACTIVE)
            .stream()
            .limit(10)
            .map(item -> {
                String primaryImageUrl = item.getImages().isEmpty() ? null :
                    item.getImages().get(0).getImageUrl();
                    
                return MostBiddedItemResponse.builder()
                    .itemId(item.getItemId())
                    .itemTitle(item.getTitle())
                    .numberOfBids(item.getBids().size())
                    .finalPrice(item.getCurrentPrice())
                    .sellerUsername(item.getUser().getUsername())
                    .status(item.getStatus().toString())
                    .primaryImageUrl(primaryImageUrl)
                    .auctionEndTime(item.getAuctionEndTime())
                    .build();
            })
            .collect(Collectors.toList());
    }
} 