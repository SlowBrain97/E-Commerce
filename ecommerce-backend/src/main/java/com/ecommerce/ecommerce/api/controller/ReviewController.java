package com.ecommerce.ecommerce.api.controller;

import com.ecommerce.ecommerce.api.dto.common.PageResponse;
import com.ecommerce.ecommerce.api.dto.review.CreateReviewRequest;
import com.ecommerce.ecommerce.api.dto.review.ReviewResponse;
import com.ecommerce.ecommerce.api.dto.common.ApiResponse;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.Review;
import com.ecommerce.ecommerce.core.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final DtoMapper mapper;
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication) {

        Review response = reviewService.createReview(authentication,request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Review created successfully", mapper.toReviewResponseDTO(response)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, size);

        // For public access, only show approved reviews unless status is specified
        if (status == null || status.isEmpty()) {
            Page<Review> response = reviewService.getApprovedReviewsByProduct(productId, pageable);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Product reviews retrieved successfully", mapper.toPageDto(response, mapper::toReviewResponseDTO)));
        } else {
            // For admin or internal use, allow filtering by status
            Page<Review> response = reviewService.getReviewsByProduct(productId, pageable);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Product reviews retrieved successfully", mapper.toPageDto(response, mapper::toReviewResponseDTO)));
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @PathVariable Long reviewId,
            Authentication authentication) {

        Review response = reviewService.getReview(reviewId, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Review retrieved successfully", mapper.toReviewResponseDTO(response)));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication) {

        Review response = reviewService.updateReview(reviewId, request, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Review updated successfully", mapper.toReviewResponseDTO(response)));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {

        reviewService.deleteReview(reviewId, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Review deleted successfully", "Review deleted successfully"));
    }

    @PostMapping("/{reviewId}/helpful")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> markReviewHelpful(
            @PathVariable Long reviewId,
            Authentication authentication) {

        reviewService.markReviewAsHelpful(reviewId, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Review marked as helpful successfully", "Review marked as helpful"));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getCurrentUserReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> response = reviewService.getCurrentUserReviews(pageable, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User reviews retrieved successfully", response));
    }

    // Admin only endpoints

    @GetMapping("/admin/pending")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> response = reviewService.getPendingReviews(pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Pending reviews retrieved successfully", response));
    }

    @PutMapping("/{reviewId}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReviewStatus(
            @PathVariable Long reviewId,
            @RequestParam String status,
            Authentication authentication) {
        Map<Integer,Integer> map = new LinkedHashMap<>();
        ReviewResponse response = reviewService.updateReviewStatus(reviewId, status, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Review status updated successfully", response));
    }
}
