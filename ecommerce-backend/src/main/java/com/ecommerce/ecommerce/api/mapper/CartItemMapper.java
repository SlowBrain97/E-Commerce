package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.cart.AddToCartRequest;
import com.ecommerce.ecommerce.api.dto.cart.CartItemResponse;
import com.ecommerce.ecommerce.api.dto.cart.CartResponse;
import com.ecommerce.ecommerce.api.dto.cart.UpdateCartItemRequest;
import com.ecommerce.ecommerce.core.domain.entity.CartItem;
import com.ecommerce.ecommerce.api.mapper.config.MapperConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper interface for CartItem entity and DTOs.
 * Uses MapStruct to automatically generate implementation.
 */
@Mapper(config = MapperConfiguration.class)
public interface CartItemMapper {

    // CartItem to CartItemResponse mapping

    @Mapping(target = "productId", expression = "java(cartItem.getProduct().getId().toString())")
    @Mapping(target = "productName", expression = "java(cartItem.getProduct().getName())")
    @Mapping(target = "productImage", expression = "java(cartItem.getProduct().getPrimaryImageUrl())")
    @Mapping(target = "price", expression = "java(cartItem.getUnitPrice())")
    @Mapping(target = "maxQuantity", expression = "java(getMaxQuantity(cartItem))")
    @Mapping(target = "variantId", expression = "java(cartItem.getProductVariant() != null ? cartItem.getProductVariant().getId().toString() : null)")
    @Mapping(target = "variantName", expression = "java(cartItem.getVariantDisplayName())")
    CartItemResponse cartItemToCartItemResponse(CartItem cartItem);

    @Named("toListCartItemResponse")
    List<CartItemResponse> toListCartItemResponse(List<CartItem> cartItems);


    default CartResponse cartItemsToCartResponse(List<CartItem> cartItems){
        if (cartItems == null) {return null;}
        List<CartItemResponse> itemResponseList = toListCartItemResponse(cartItems);
        return CartResponse.builder().items(itemResponseList)
                .totalItems(itemResponseList.stream().mapToInt(CartItemResponse::getQuantity).sum())
                .currency("JPY")
                .totalPrice(itemResponseList.stream().map(CartItemResponse::getPrice).reduce(BigDecimal.ZERO,BigDecimal::add))
                .build();
    };

    // AddToCartRequest to CartItem mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Will be set in service
    @Mapping(target = "product", ignore = true) // Will be set in service
    @Mapping(target = "productVariant", ignore = true) // Will be set in service if needed
    @Mapping(target = "addedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CartItem addToCartRequestToCartItem(AddToCartRequest request);

    // UpdateCartItemRequest to CartItem mapping (for updating existing item)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "productVariant", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CartItem updateCartItemRequestToCartItem(UpdateCartItemRequest request);

    // Helper method for max quantity
    default Integer getMaxQuantity(CartItem cartItem) {
        if (cartItem.getProductVariant() != null) {
            return cartItem.getProductVariant().getStockQuantity();
        }
        return cartItem.getProduct().getStockQuantity();
    }

}
