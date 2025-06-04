package com.barterswap.service;

import com.barterswap.dto.feedback.CreateFeedbackRequest;
import com.barterswap.dto.feedback.FeedbackResponse;
import com.barterswap.dto.feedback.UserReputationResponse;
import com.barterswap.dto.transaction.TransactionResponse;
import com.barterswap.entity.Feedback;
import com.barterswap.entity.Transaction;
import com.barterswap.entity.User;
import com.barterswap.enums.FeedbackScore;
import com.barterswap.enums.TransactionStatus;
import com.barterswap.exception.FeedbackException;
import com.barterswap.repository.FeedbackRepository;
import com.barterswap.repository.TransactionRepository;
import com.barterswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final VirtualCurrencyService virtualCurrencyService;

    @Transactional
    public FeedbackResponse createFeedback(CreateFeedbackRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User giver = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Find the transaction
            Transaction transaction = transactionRepository.findById(request.getTransactionId())
                    .orElseThrow(() -> new FeedbackException("Transaction not found"));

            // Validate that the user is the buyer in this transaction
            if (!transaction.getBuyer().getUserId().equals(giver.getUserId())) {
                throw new FeedbackException("You can only leave feedback for transactions where you are the buyer");
            }

            // Validate transaction is completed
            if (transaction.getStatus() != TransactionStatus.COMPLETED) {
                throw new FeedbackException("You can only leave feedback for completed transactions");
            }

            // Check if feedback already exists for this transaction
            if (feedbackRepository.existsByTransaction(transaction)) {
                throw new FeedbackException("Feedback has already been left for this transaction");
            }

            // Create feedback
            Feedback feedback = Feedback.builder()
                    .giver(giver)
                    .receiver(transaction.getSeller())
                    .transaction(transaction)
                    .score(request.getScore())
                    .comment(request.getComment())
                    .build();

            feedback = feedbackRepository.save(feedback);
            log.info("Feedback created: {} for transaction {}", feedback.getFeedbackId(), transaction.getTransactionId());

            // Update seller's reputation
            updateUserReputation(transaction.getSeller());

            // Give virtual currency reward/penalty to seller based on star rating
            if (request.getScore() == FeedbackScore.FOUR_STARS || request.getScore() == FeedbackScore.FIVE_STARS) {
                virtualCurrencyService.addPositiveFeedbackReward(transaction.getSeller());
                log.info("Added positive feedback reward to seller {}", transaction.getSeller().getUsername());
            } else if (request.getScore() == FeedbackScore.ONE_STAR || request.getScore() == FeedbackScore.TWO_STARS) {
                virtualCurrencyService.addNegativeFeedbackPenalty(transaction.getSeller());
                log.info("Added negative feedback penalty to seller {}", transaction.getSeller().getUsername());
            }

            return mapToFeedbackResponse(feedback);
        } catch (Exception e) {
            log.error("Error creating feedback: {}", e.getMessage(), e);
            throw new FeedbackException("Failed to create feedback: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackForUser(Integer userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new FeedbackException("User not found"));

            List<Feedback> feedbacks = feedbackRepository.findByReceiver(user);
            return feedbacks.stream()
                    .map(this::mapToFeedbackResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting feedback for user {}: {}", userId, e.getMessage(), e);
            throw new FeedbackException("Failed to get feedback: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getMyGivenFeedback() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            List<Feedback> feedbacks = feedbackRepository.findByGiver(user);
            return feedbacks.stream()
                    .map(this::mapToFeedbackResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting given feedback for user: {}", e.getMessage(), e);
            throw new FeedbackException("Failed to get given feedback: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public UserReputationResponse getUserReputation(Integer userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new FeedbackException("User not found"));

            Long totalReviews = feedbackRepository.countByReceiver(user);
            Double averageRating = feedbackRepository.calculateAverageRating(user);
            
            // Count reviews for each star rating
            Integer oneStarCount = feedbackRepository.countByReceiverAndScore(user, FeedbackScore.ONE_STAR);
            Integer twoStarCount = feedbackRepository.countByReceiverAndScore(user, FeedbackScore.TWO_STARS);
            Integer threeStarCount = feedbackRepository.countByReceiverAndScore(user, FeedbackScore.THREE_STARS);
            Integer fourStarCount = feedbackRepository.countByReceiverAndScore(user, FeedbackScore.FOUR_STARS);
            Integer fiveStarCount = feedbackRepository.countByReceiverAndScore(user, FeedbackScore.FIVE_STARS);

            // Calculate reputation based on average rating (scaled to match previous system)
            Integer newReputation = averageRating != null ? (int)((averageRating - 1) * 25) : 0;
            user.setReputation(newReputation);
            userRepository.save(user);

            return UserReputationResponse.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .reputation(user.getReputation())
                    .totalReviews(totalReviews)
                    .averageRating(averageRating)
                    .oneStarCount(oneStarCount)
                    .twoStarCount(twoStarCount)
                    .threeStarCount(threeStarCount)
                    .fourStarCount(fourStarCount)
                    .fiveStarCount(fiveStarCount)
                    .build();
        } catch (Exception e) {
            log.error("Error getting user reputation for user {}: {}", userId, e.getMessage(), e);
            throw new FeedbackException("Failed to get user reputation: " + e.getMessage());
        }
    }

    @Transactional
    private void updateUserReputation(User user) {
        Double averageRating = feedbackRepository.calculateAverageRating(user);
        
        // Calculate reputation based on average rating (scaled to match previous system)
        // This will give a range of 0-100, where:
        // 1 star = 0 reputation
        // 3 stars = 50 reputation
        // 5 stars = 100 reputation
        Integer newReputation = averageRating != null ? (int)((averageRating - 1) * 25) : 0;
        user.setReputation(newReputation);
        userRepository.save(user);
        
        log.info("Updated reputation for user {} to {} (average rating: {})", 
                user.getUsername(), newReputation, averageRating);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsEligibleForFeedback() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User buyer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Get all completed transactions where the user was the buyer
            List<Transaction> completedTransactions = transactionRepository
                    .findByBuyerAndStatus(buyer, TransactionStatus.COMPLETED);

            // Filter out transactions that already have feedback and map to DTO
            return completedTransactions.stream()
                    .filter(transaction -> !feedbackRepository.existsByTransaction(transaction))
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting transactions eligible for feedback: {}", e.getMessage(), e);
            throw new FeedbackException("Failed to get eligible transactions: " + e.getMessage());
        }
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .buyer(TransactionResponse.UserSummary.builder()
                        .userId(transaction.getBuyer().getUserId())
                        .username(transaction.getBuyer().getUsername())
                        .build())
                .seller(TransactionResponse.UserSummary.builder()
                        .userId(transaction.getSeller().getUserId())
                        .username(transaction.getSeller().getUsername())
                        .build())
                .item(TransactionResponse.ItemSummary.builder()
                        .itemId(transaction.getItem().getItemId())
                        .title(transaction.getItem().getTitle())
                        .primaryImageUrl(transaction.getItem().getImages().isEmpty() ? null :
                                transaction.getItem().getImages().get(0).getImageUrl())
                        .build())
                .price(transaction.getPrice())
                .status(transaction.getStatus())
                .transactionDate(transaction.getTransactionDate())
                .hasFeedback(feedbackRepository.existsByTransaction(transaction))
                .build();
    }

    private FeedbackResponse mapToFeedbackResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .giverId(feedback.getGiver().getUserId())
                .giverUsername(feedback.getGiver().getUsername())
                .receiverId(feedback.getReceiver().getUserId())
                .receiverUsername(feedback.getReceiver().getUsername())
                .transactionId(feedback.getTransaction().getTransactionId())
                .itemId(feedback.getTransaction().getItem().getItemId())
                .itemTitle(feedback.getTransaction().getItem().getTitle())
                .score(feedback.getScore())
                .comment(feedback.getComment())
                .timestamp(feedback.getTimestamp())
                .build();
    }
} 