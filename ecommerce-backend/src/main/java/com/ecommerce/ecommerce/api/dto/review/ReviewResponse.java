package com.ecommerce.ecommerce.api.dto.review;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer rating;
    private String title;
    private String comment;
    private Boolean isVerifiedPurchase;
    private String status;
    private Boolean isFeatured;
    private Integer helpfulVotes;
    private Integer totalVotes;
    private Double helpfulnessPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
