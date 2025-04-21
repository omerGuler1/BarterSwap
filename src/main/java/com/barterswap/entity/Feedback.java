package com.barterswap.entity;

import com.barterswap.converter.FeedbackScoreConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import com.barterswap.enums.FeedbackScore;

@Entity
@Table(name = "feedback",
       indexes = {
           @Index(name = "idx_feedback_receiver_id", columnList = "receiver_id"),
           @Index(name = "idx_feedback_transaction_id", columnList = "transaction_id")
       })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @ManyToOne
    @JoinColumn(name = "giver_id", nullable = false)
    private User giver;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private Transaction transaction;

    @Convert(converter = FeedbackScoreConverter.class)
    @Column(nullable = false)
    private FeedbackScore score;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
} 