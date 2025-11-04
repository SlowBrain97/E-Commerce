package com.ecommerce.ecommerce.api.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    private Long productVariantId; // Optional, for variant products

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String notes; // Special instructions for this item
}
