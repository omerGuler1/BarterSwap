package com.barterswap.entity;

import com.barterswap.enums.ItemCategory;
import com.barterswap.enums.ItemCondition;
import com.barterswap.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "item",
       indexes = {
           @Index(name = "idx_item_user_id", columnList = "user_id"),
           @Index(name = "idx_item_category", columnList = "category"),
           @Index(name = "idx_item_is_active", columnList = "is_active"),
           @Index(name = "idx_item_is_deleted", columnList = "is_deleted"),
           @Index(name = "idx_item_status", columnList = "status")
       })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "item_category")
    private ItemCategory category;

    @Column(name = "starting_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal startingPrice;

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "item_condition")
    private ItemCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "item_status")
    @Builder.Default
    private ItemStatus status = ItemStatus.ACTIVE;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ItemImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private Transaction transaction;
} 