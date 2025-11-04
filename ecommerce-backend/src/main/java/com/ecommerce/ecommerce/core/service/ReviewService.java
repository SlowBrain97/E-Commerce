package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.api.dto.review.CreateReviewRequest;
import com.ecommerce.ecommerce.api.dto.review.ReviewResponse;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.Review;
import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.core.exception.BusinessException;
import com.ecommerce.ecommerce.core.exception.ErrorCode;
import com.ecommerce.ecommerce.core.repository.ReviewRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Review entity operations.
 * Handles product review management, moderation, and analytics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductService productService;
    private final DtoMapper dtoMapper;
    /**
     * Create a new review
     */
    @Transactional
    public Review createReview(Authentication authentication, CreateReviewRequest request) {
        User user = userService.getCurrentUser(authentication);
        Product product = productService.getProductById(request.getProductId()).orElseThrow(()-> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        log.info("Creating review for product {} by user {}", product.getId(), user.getId());

        // Check if user already reviewed this product
        Optional<Review> existingReview = reviewRepository.findByUserAndProduct(user, product);
        if (existingReview.isPresent()) {
            throw new RuntimeException("User has already reviewed this product");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .status(Review.ReviewStatus.PENDING) // Requires moderation
                .isVerifiedPurchase(false) // Will be updated after order verification
                .helpfulVotes(0)
                .totalVotes(0)
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review created: {}", savedReview.getId());

        return savedReview;
    }

    /**
     * Get reviews by product with pagination
     */
    public Page<Review> getReviewsByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findApprovedByProductIdOrderByCreatedAtDesc(productId, pageable);
    }

    /**
     * Get approved reviews by product
     */
    public Page<Review> getApprovedReviewsByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findApprovedByProductIdOrderByCreatedAtDesc(productId, pageable);
    }

    /**
     * Get reviews by user
     */
    public Page<Review> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Approve review
     */
    @Transactional
    public Review approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setStatus(Review.ReviewStatus.APPROVED);
        Review savedReview = reviewRepository.save(review);

        log.info("Review approved: {}", reviewId);
        return savedReview;
    }

    /**
     * Reject review
     */
    @Transactional
    public Review rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setStatus(Review.ReviewStatus.REJECTED);
        Review savedReview = reviewRepository.save(review);

        log.info("Review rejected: {}", reviewId);
        return savedReview;
    }

    /**
     * Flag review for moderation
     */
    @Transactional
    public Review flagReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setStatus(Review.ReviewStatus.FLAGGED);
        Review savedReview = reviewRepository.save(review);

        log.info("Review flagged: {}", reviewId);
        return savedReview;
    }

    /**
     * Update review helpfulness votes
     */
    @Transactional
    public Review updateHelpfulVotes(Long reviewId, boolean isHelpful) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (isHelpful) {
            review.incrementHelpfulVotes();
        } else {
            review.incrementTotalVotes();
        }

        return reviewRepository.save(review);
    }

    /**
     * Get average rating for product
     */
    public Double getAverageRating(Long productId) {
        Double average = reviewRepository.getAverageRatingByProduct(productId);
        return average != null ? average : 0.0;
    }

    /**
     * Get rating distribution for product
     */
    public List<Object[]> getRatingDistribution(Long productId) {
        return reviewRepository.getRatingDistributionByProduct(productId);
    }

    /**
     * Get reviews requiring moderation
     */
    public List<Review> getReviewsRequiringModeration(Pageable pageable) {
        return reviewRepository.findReviewsRequiringModeration(pageable);
    }

    /**
     * Get flagged reviews
     */
    public List<Review> getFlaggedReviews(Pageable pageable) {
        return reviewRepository.findFlaggedReviews(pageable);
    }

    /**
     * Mark review as verified purchase
     */
    @Transactional
    public Review markAsVerifiedPurchase(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setIsVerifiedPurchase(true);
        Review savedReview = reviewRepository.save(review);

        log.info("Review marked as verified purchase: {}", reviewId);
        return savedReview;
    }

    /**
     * Feature a review
     */
    @Transactional
    public Review featureReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setIsFeatured(true);
        Review savedReview = reviewRepository.save(review);

        log.info("Review featured: {}", reviewId);
        return savedReview;
    }

    /**
     * Unfeature a review
     */
    @Transactional
    public Review unfeatureReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setIsFeatured(false);
        Review savedReview = reviewRepository.save(review);

        log.info("Review unfeatured: {}", reviewId);
        return savedReview;
    }

    /**
     * Get review statistics for product
     */
    public ReviewStats getReviewStats(Long productId) {
        long totalReviews = reviewRepository.countByProductIdAndStatus(productId, Review.ReviewStatus.APPROVED);
        long verifiedReviews = reviewRepository.countByProductIdAndStatusAndIsVerifiedPurchase(
                productId, Review.ReviewStatus.APPROVED, true);
        Double averageRating = getAverageRating(productId);

        return ReviewStats.builder()
                .totalReviews(totalReviews)
                .verifiedReviews(verifiedReviews)
                .averageRating(averageRating)
                .build();
    }

    /**
     * Check if user can review product
     */
    public boolean canUserReviewProduct(Long userId, Long productId) {
        return reviewRepository.canUserReviewProduct(userId, productId);
    }

    /**
     * Get helpful reviews
     */
    public List<Review> getHelpfulReviews(Double threshold, Pageable pageable) {
        return reviewRepository.findHelpfulReviews(threshold, pageable);
    }

    /**
     * Get recent reviews across all products
     */
    public List<Review> getRecentReviews(Pageable pageable) {
        return reviewRepository.findRecentReviews(pageable);
    }

    /**
     * Get monthly review data for analytics
     */
    public List<Object[]> getMonthlyReviewData(LocalDateTime since) {
        return reviewRepository.getMonthlyReviewData(since);
    }
    public Review getReview(Long reviewId, Authentication authentication) {
        return reviewRepository.findById(reviewId).orElseThrow(()-> new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "Review not found"));
    }

    public Review updateReview(Long reviewId, CreateReviewRequest request, Authentication authentication) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // Check if the current user owns this review
        User currentUser = userService.getCurrentUser(authentication);
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "You can only update your own reviews");
        }

        // Update review fields
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        log.info("Review updated: {}", reviewId);

        return savedReview;
    }

    /**
     * Delete a review (soft delete by changing status)
     */
    @Transactional
    public void deleteReview(Long reviewId, Authentication authentication) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // Check if the current user owns this review or is admin
        User currentUser = userService.getCurrentUser(authentication);
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "You can only delete your own reviews");
        }

        // Soft delete by setting status to DELETED (assuming we want to keep data for analytics)
        // If you want hard delete, uncomment the next line and remove the status change
        // reviewRepository.delete(review);

        review.setStatus(Review.ReviewStatus.REJECTED); // Using REJECTED as soft delete equivalent
        reviewRepository.save(review);

    }

    /**
     * Mark a review as helpful
     */
    @Transactional
    public void markReviewAsHelpful(Long reviewId, Authentication authentication) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // Check if review is approved and visible
        if (review.getStatus() != Review.ReviewStatus.APPROVED) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND, "Review not found or not available");
        }

        User currentUser = userService.getCurrentUser(authentication);

        // TODO: Implement logic to track which users found which reviews helpful
        // For now, just increment helpful votes
        review.incrementHelpfulVotes();
        reviewRepository.save(review);

    }

    /**
     * Get current user's reviews with pagination
     */
    public Page<ReviewResponse> getCurrentUserReviews(Pageable pageable, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        Page<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);

        return reviews.map(dtoMapper::toReviewResponseDTO);
    }

    /**
     * Get pending reviews for admin moderation
     */
    public Page<ReviewResponse> getPendingReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByStatusOrderByCreatedAtDesc(Review.ReviewStatus.PENDING, pageable);
        return reviews.map(dtoMapper::toReviewResponseDTO);
    }

    /**
     * Update review status (admin only)
     */
    @Transactional
    public ReviewResponse updateReviewStatus(Long reviewId, String status, Authentication authentication) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()-> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        // TODO: Add admin role check here
        // if (!isAdmin(authentication)) {
        //     throw new BusinessException(ErrorCode.ACCESS_DENIED, "Admin access required");
        // }

        try {
            Review.ReviewStatus newStatus = Review.ReviewStatus.valueOf(status.toUpperCase());
            review.setStatus(newStatus);
            Review savedReview = reviewRepository.save(review);

            log.info("Review {} status updated to {} by admin", reviewId, status);
            return dtoMapper.toReviewResponseDTO(savedReview);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Invalid status: " + status);
        }
    }

    /**
     * DTO for review statistics
     */
    @Setter
    @Getter
    public static class ReviewStats {
        // Getters and setters
        private long totalReviews;
        private long verifiedReviews;
        private Double averageRating;

        public static ReviewStatsBuilder builder() {
            return new ReviewStatsBuilder();
        }

        public static class ReviewStatsBuilder {
            private final ReviewStats reviewStats = new ReviewStats();

            public ReviewStatsBuilder totalReviews(long totalReviews) {
                reviewStats.totalReviews = totalReviews;
                return this;
            }

            public ReviewStatsBuilder verifiedReviews(long verifiedReviews) {
                reviewStats.verifiedReviews = verifiedReviews;
                return this;
            }

            public ReviewStatsBuilder averageRating(Double averageRating) {
                reviewStats.averageRating = averageRating;
                return this;
            }

            public ReviewStats build() {
                return reviewStats;
            }
        }
    }
}
