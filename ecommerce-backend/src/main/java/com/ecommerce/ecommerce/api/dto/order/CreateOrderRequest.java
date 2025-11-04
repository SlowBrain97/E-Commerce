package com.ecommerce.ecommerce.api.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateOrderRequest {

    @Valid
    @NotEmpty(message = "Order items are required")
    private List<OrderItemRequest> items;

    @Valid
    @NotNull(message = "Shipping address is required")
    private AddressDTO shippingAddress;

    @Valid
    private AddressDTO billingAddress; // Optional, defaults to shipping address

    private String paymentMethodId;

    private String notes;

}
