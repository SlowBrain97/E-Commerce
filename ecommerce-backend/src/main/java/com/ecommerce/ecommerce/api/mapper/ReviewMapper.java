package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.review.CreateReviewRequest;
import com.ecommerce.ecommerce.api.dto.review.ReviewResponse;
import com.ecommerce.ecommerce.core.domain.entity.Review;
import com.ecommerce.ecommerce.api.mapper.config.MapperConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for Review entity and DTOs.
 * Uses MapStruct to automatically generate implementation.
 */
@Mapper(config = MapperConfiguration.class)
public interface ReviewMapper {

    // Review to ReviewResponse mapping
    @Mapping(target = "productId", expression = "java(review.getProduct().getId())")
    @Mapping(target = "productName", expression = "java(review.getProduct().getName())")
    @Mapping(target = "userId", expression = "java(review.getUser().getId())")
    @Mapping(target = "userName", expression = "java(review.getUser().getFullName())")
    @Mapping(target = "userAvatar", source = "user.avatarUrl")
    @Mapping(target = "status", expression = "java(review.getStatus().toString())")
    @Mapping(target = "helpfulnessPercentage", expression = "java(review.getHelpfulnessPercentage())")
    ReviewResponse reviewToReviewResponse(Review review);

    // ReviewResponse to Review entity mapping (reverse)
    @Mapping(target = "product", ignore = true) // Will be set in service
    @Mapping(target = "user", ignore = true) // Will be set in service
    @Mapping(target = "status", ignore = true) // Will be set based on business logic
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review reviewResponseToReview(ReviewResponse reviewResponse);

    // List mapping for batch operations
    @Mapping(target = "productId", expression = "java(review.getProduct().getId())")
    @Mapping(target = "productName", expression = "java(review.getProduct().getName())")
    @Mapping(target = "userId", expression = "java(review.getUser().getId())")
    @Mapping(target = "userName", expression = "java(review.getUser().getFullName())")
    @Mapping(target = "userAvatar", source = "user.avatarUrl")
    @Mapping(target = "status", expression = "java(review.getStatus().toString())")
    @Mapping(target = "helpfulnessPercentage", expression = "java(review.getHelpfulnessPercentage())")
    List<ReviewResponse> reviewsToReviewResponses(List<Review> reviews);

    // CreateReviewRequest to Review entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true) // Will be set in service
    @Mapping(target = "user", ignore = true) // Will be set in service
    @Mapping(target = "isVerifiedPurchase", constant = "false") // Default value
    @Mapping(target = "status", constant = "PENDING") // Reviews need moderation
    @Mapping(target = "isFeatured", constant = "false") // Default value
    @Mapping(target = "helpfulVotes", constant = "0") // Default value
    @Mapping(target = "totalVotes", constant = "0") // Default value
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review createReviewRequestToReview(CreateReviewRequest request);

}

