package com.ecommerce.ecommerce.core.repository;

import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.Review;
import com.ecommerce.ecommerce.core.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity operations.
 * Provides methods for product review management and analytics.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find reviews by product and status with pagination
     */
    Page<Review> findByProductIdAndStatus(Long productId, Review.ReviewStatus status, Pageable pageable);
    /**
     * Find approved reviews by product with pagination
     */
    Page<Review> findApprovedByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    /**
     * Find reviews by user with pagination
     */
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find review by user and product (to prevent duplicate reviews)
     */
    Optional<Review> findByUserAndProduct(User user, Product product);

    /**
     * Find reviews by rating
     */
    Page<Review> findByRatingAndStatusOrderByCreatedAtDesc(Integer rating, Review.ReviewStatus status, Pageable pageable);

    /**
     * Find reviews by status
     */
    Page<Review> findByStatusOrderByCreatedAtDesc(Review.ReviewStatus status, Pageable pageable);

    /**
     * Find featured reviews by product
     */
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED' AND r.isFeatured = true ORDER BY r.helpfulVotes DESC")
    List<Review> findFeaturedReviewsByProduct(@Param("productId") Long productId, Pageable pageable);

    /**
     * Find verified purchase reviews by product
     */
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED' AND r.isVerifiedPurchase = true ORDER BY r.createdAt DESC")
    List<Review> findVerifiedPurchaseReviewsByProduct(@Param("productId") Long productId, Pageable pageable);

    /**
     * Get average rating for a product
     */
    @Query("SELECT AVG(CAST(r.rating AS FLOAT)) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED'")
    Double getAverageRatingByProduct(@Param("productId") Long productId);

    /**
     * Get rating distribution for a product
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED' GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionByProduct(@Param("productId") Long productId);

    /**
     * Count reviews by status
     */
    long countByStatus(Review.ReviewStatus status);

    /**
     * Count reviews by product
     */
    long countByProductIdAndStatus(Long productId, Review.ReviewStatus status);

    /**
     * Count verified purchase reviews
     */
    long countByProductIdAndStatusAndIsVerifiedPurchase(Long productId, Review.ReviewStatus status, Boolean isVerifiedPurchase);

    /**
     * Find reviews requiring moderation
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'PENDING' ORDER BY r.createdAt ASC")
    List<Review> findReviewsRequiringModeration(Pageable pageable);

    /**
     * Find reviews flagged for attention
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'FLAGGED' ORDER BY r.createdAt DESC")
    List<Review> findFlaggedReviews(Pageable pageable);

    /**
     * Get review statistics by product
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED' GROUP BY r.rating")
    List<Object[]> getReviewStatsByProduct(@Param("productId") Long productId);

    /**
     * Find helpful reviews (high helpful votes ratio)
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'APPROVED' AND r.totalVotes > 0 AND " +
           "(CAST(r.helpfulVotes AS FLOAT) / r.totalVotes) >= :threshold ORDER BY " +
           "(CAST(r.helpfulVotes AS FLOAT) / r.totalVotes) DESC, r.helpfulVotes DESC")
    List<Review> findHelpfulReviews(@Param("threshold") Double threshold, Pageable pageable);

    /**
     * Find recent reviews across all products
     */
    @Query("SELECT r FROM Review r WHERE r.status = 'APPROVED' ORDER BY r.createdAt DESC")
    List<Review> findRecentReviews(Pageable pageable);

    /**
     * Get monthly review count for analytics
     */
    @Query("SELECT YEAR(r.createdAt), MONTH(r.createdAt), COUNT(r) " +
           "FROM Review r WHERE r.status = 'APPROVED' AND r.createdAt >= :since " +
           "GROUP BY YEAR(r.createdAt), MONTH(r.createdAt) " +
           "ORDER BY YEAR(r.createdAt), MONTH(r.createdAt)")
    List<Object[]> getMonthlyReviewData(@Param("since") java.time.LocalDateTime since);

    /**
     * Find reviews by user for admin management
     */
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Review> findReviewsByUserForAdmin(@Param("userId") Long userId);

    /**
     * Find average rating by product ID (for dashboard)
     */
    Optional<Double> findAverageRatingByProductId(Long productId);

    /**
     * Check if user can review a product (has purchased it)
     */
    @Query(value = "SELECT COUNT(oi) > 0 FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.user.id = :userId AND oi.product.id = :productId AND o.status IN ('DELIVERED', 'SHIPPED')", nativeQuery = true)
    boolean canUserReviewProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
