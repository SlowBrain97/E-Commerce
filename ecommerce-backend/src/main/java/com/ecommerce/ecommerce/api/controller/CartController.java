package com.ecommerce.ecommerce.api.controller;

import com.ecommerce.ecommerce.api.dto.cart.AddToCartRequest;
import com.ecommerce.ecommerce.api.dto.cart.CartItemResponse;
import com.ecommerce.ecommerce.api.dto.cart.CartResponse;
import com.ecommerce.ecommerce.api.dto.cart.UpdateCartItemRequest;
import com.ecommerce.ecommerce.api.dto.common.ApiResponse;
import com.ecommerce.ecommerce.api.dto.product.ProductVariantDTO;
import com.ecommerce.ecommerce.core.service.CartItemService;
import com.ecommerce.ecommerce.core.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartItemService cartItemService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {
        CartResponse response = cartItemService.getCartForUser(authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Cart retrieved successfully", response));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @RequestParam(required = false) ProductVariantDTO productVariantDTO,
            Authentication authentication) {
        CartItemResponse response = cartItemService.addToCart(request, productVariantDTO, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Item added to cart successfully", response));
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {
        CartItemResponse response = cartItemService.updateCartItem(itemId, request, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Cart item updated successfully", response));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(
            @PathVariable Long itemId,
            Authentication authentication) {
        cartItemService.removeFromCart(itemId, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Item removed from cart successfully", "Item removed successfully"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearCart(Authentication authentication) {
        cartItemService.clearCart(authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Cart cleared successfully", "Cart cleared successfully"));
    }

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<CartResponse>> syncCart(
            @RequestBody List<AddToCartRequest> items,
            Authentication authentication) {
        CartResponse response = cartItemService.syncCart(items, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Cart synchronized successfully", response));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartCount(Authentication authentication) {
        Integer response = cartItemService.getCartItemCount(authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Cart count retrieved successfully", response));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> getCartTotal(Authentication authentication) {
        java.math.BigDecimal response = cartItemService.getCartTotal(authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Cart total retrieved successfully", response));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateCart(Authentication authentication) {
        String response = cartItemService.validateCart(authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Cart validation completed", response));
    }
}
