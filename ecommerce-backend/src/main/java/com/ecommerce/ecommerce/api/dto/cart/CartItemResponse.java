package com.ecommerce.ecommerce.api.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    private String id;
    private String productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private Integer maxQuantity;
    private String variantId;
    private String variantName;
    private LocalDateTime addedAt;
}
