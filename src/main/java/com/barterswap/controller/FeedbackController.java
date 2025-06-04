package com.barterswap.controller;

import com.barterswap.dto.feedback.CreateFeedbackRequest;
import com.barterswap.dto.feedback.FeedbackResponse;
import com.barterswap.dto.feedback.UserReputationResponse;
import com.barterswap.dto.transaction.TransactionResponse;
import com.barterswap.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody CreateFeedbackRequest request) {
        log.info("Creating feedback for transaction: {}", request.getTransactionId());
        FeedbackResponse response = feedbackService.createFeedback(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackForUser(@PathVariable Integer userId) {
        log.info("Getting feedback for user: {}", userId);
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbackForUser(userId);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/my-given")
    public ResponseEntity<List<FeedbackResponse>> getMyGivenFeedback() {
        log.info("Getting given feedback for authenticated user");
        List<FeedbackResponse> feedbacks = feedbackService.getMyGivenFeedback();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/reputation/{userId}")
    public ResponseEntity<UserReputationResponse> getUserReputation(@PathVariable Integer userId) {
        log.info("Getting reputation for user: {}", userId);
        UserReputationResponse reputation = feedbackService.getUserReputation(userId);
        return ResponseEntity.ok(reputation);
    }

    @GetMapping("/eligible-transactions")
    public ResponseEntity<List<TransactionResponse>> getEligibleTransactions() {
        log.info("Getting transactions eligible for feedback");
        List<TransactionResponse> transactions = feedbackService.getTransactionsEligibleForFeedback();
        return ResponseEntity.ok(transactions);
    }
} 