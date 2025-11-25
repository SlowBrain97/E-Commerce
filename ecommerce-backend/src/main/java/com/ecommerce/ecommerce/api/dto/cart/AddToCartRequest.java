package com.ecommerce.ecommerce.api.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddToCartRequest {

    @NotNull(message = "Product ID is required")
    private String productId;
    
    private String variantId;

    @Builder.Default
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;
}
