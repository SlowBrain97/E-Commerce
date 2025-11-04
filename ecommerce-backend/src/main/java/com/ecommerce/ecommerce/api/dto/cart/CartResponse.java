package com.ecommerce.ecommerce.api.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartResponse {

    private List<CartItemResponse> items;
    private Integer totalItems;
    private BigDecimal totalPrice;
    private String currency;

}
