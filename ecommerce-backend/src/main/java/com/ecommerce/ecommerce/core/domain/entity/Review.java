package com.ecommerce.ecommerce.core.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Review entity representing a product review by a user.
 * Supports rating, comments, and moderation features.
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_product", columnList = "product_id"),
    @Index(name = "idx_review_user", columnList = "user_id"),
    @Index(name = "idx_review_rating", columnList = "rating"),
    @Index(name = "idx_review_status", columnList = "status"),
    @Index(name = "idx_review_created", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Size(max = 1000)
    @Column(name = "title")
    private String title;

    @Size(max = 5000)
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Builder.Default
    @Column(name = "is_verified_purchase", nullable = false)
    private Boolean isVerifiedPurchase = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReviewStatus status;

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Min(0)
    @Column(name = "helpful_votes")
    private Integer helpfulVotes;

    @Min(0)
    @Column(name = "total_votes")
    private Integer totalVotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculate helpfulness percentage
     */
    public Double getHelpfulnessPercentage() {
        if (totalVotes == null || totalVotes == 0) {
            return 0.0;
        }
        if (helpfulVotes == null) {
            return 0.0;
        }
        return (double) helpfulVotes / totalVotes * 100;
    }

    /**
     * Check if review is approved and visible
     */
    public boolean isVisible() {
        return ReviewStatus.APPROVED.equals(status);
    }

    /**
     * Increment helpful votes
     */
    public void incrementHelpfulVotes() {
        this.helpfulVotes = (this.helpfulVotes == null) ? 1 : this.helpfulVotes + 1;
        incrementTotalVotes();
    }

    /**
     * Increment total votes
     */
    public void incrementTotalVotes() {
        this.totalVotes = (this.totalVotes == null) ? 1 : this.totalVotes + 1;
    }

    public enum ReviewStatus {
        PENDING,    // Waiting for moderation
        APPROVED,   // Approved and visible
        REJECTED,   // Rejected by moderation
        FLAGGED     // Flagged for review
    }
}
