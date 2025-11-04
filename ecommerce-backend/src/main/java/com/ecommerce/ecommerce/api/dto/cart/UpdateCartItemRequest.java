package com.ecommerce.ecommerce.api.dto.cart;

import jakarta.validation.constraints.Min;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class UpdateCartItemRequest {

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

}
