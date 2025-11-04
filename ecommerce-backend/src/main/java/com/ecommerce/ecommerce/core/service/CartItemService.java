package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.api.dto.cart.AddToCartRequest;
import com.ecommerce.ecommerce.api.dto.cart.CartItemResponse;
import com.ecommerce.ecommerce.api.dto.cart.CartResponse;
import com.ecommerce.ecommerce.api.dto.cart.UpdateCartItemRequest;
import com.ecommerce.ecommerce.api.dto.product.ProductVariantDTO;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.CartItem;
import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.ProductVariant;
import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.core.exception.BusinessException;
import com.ecommerce.ecommerce.core.exception.ErrorCode;
import com.ecommerce.ecommerce.core.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.core.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for CartItem entity operations.
 * Handles shopping cart management, item operations, and cart calculations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final DtoMapper mapper;

    /**
     * Add item to cart
     */
    @Transactional
    public CartItemResponse addToCart(AddToCartRequest request, ProductVariantDTO variant, Authentication authentication) {
        User user = authenticationService.getCurrentUser(authentication);

        Product product = productService.getProductById(Long.valueOf(request.getProductId()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product", request.getProductId()));

        log.info("Adding product {} to cart for user {}", request.getProductId(), user.getId());

        // Check if item already exists in cart
        Optional<CartItem> existingItem;
        if (variant != null) {
            ProductVariant productVariant = mapper.toProductVariantEntity(variant);
            existingItem = cartItemRepository.findByUserAndProductAndProductVariant(user, product, productVariant);
        } else {
            existingItem = cartItemRepository.findByUserAndProduct(user, product);
        }

        if (existingItem.isPresent()) {
            // Update quantity of existing item
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            CartItem savedItem = cartItemRepository.save(item);
            log.info("Updated cart item quantity: {}", savedItem.getId());
            return mapper.toCartItemResponseDTO(savedItem);
        } else {
            // Create new cart item
            CartItem cartItem = mapper.toCartItemEntity(request);
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setProductVariant(variant != null ? mapper.toProductVariantEntity(variant) : null);
            cartItem.setAddedAt(LocalDateTime.now());

            CartItem savedItem = cartItemRepository.save(cartItem);
            log.info("Added new item to cart: {}", savedItem.getId());
            return mapper.toCartItemResponseDTO(savedItem);
        }
    }
    /**
     * Update cart item
     */
    @Transactional
    public CartItemResponse updateCartItem(Long itemId, UpdateCartItemRequest request, Authentication authentication) {
        User user = authenticationService.getCurrentUser(authentication);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND, "CartItem", itemId));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Access denied to cart item");
        }

        item.setQuantity(request.getQuantity());
        CartItem savedItem = cartItemRepository.save(item);

        log.info("Updated cart item {} quantity to {}", itemId, request.getQuantity());
        return mapper.toCartItemResponseDTO(savedItem);
    }

    @Transactional
    public void removeFromCart(Long itemId, Authentication authentication) {
        User user = authenticationService.getCurrentUser(authentication);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND, "CartItem", itemId));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Access denied to cart item");
        }

        cartItemRepository.delete(item);
        log.info("Removed cart item: {}", itemId);
    }

    @Transactional
    public void clearCart(Authentication authentication) {
        User user = authenticationService.getCurrentUser(authentication);
        cartItemRepository.removeAllByUserId(user.getId());
        log.info("Cleared cart for user: {}", user.getId());
    }

    public CartResponse getCartForUser(Authentication authentication) {
        User user = authenticationService.getCurrentUser(authentication);
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByAddedAtDesc(user.getId());
        return mapper.toCartResponseDTO(cartItems);
    }
    /**
     * Sync cart with list of items
     */
    @Transactional
    public CartResponse syncCart(List<AddToCartRequest> items, Authentication authentication) {
        User user = authenticationService.getCurrentUser(authentication);

        // Clear current cart
        cartItemRepository.removeAllByUserId(user.getId());

        // Add new items
        for (AddToCartRequest item : items) {
            try {
                ProductVariant productVariant = productVariantService.getVariantById(Long.parseLong(item.getVariantId())).orElseThrow(()-> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
                addToCart(item, mapper.toProductVariantDTO(productVariant), authentication);
            } catch (Exception e) {
                log.warn("Failed to add item {} to cart during sync: {}", item.getProductId(), e.getMessage());
            }
        }

        // Return updated cart
        return getCartForUser(authentication);
    }

    public Integer getCartItemCount(Authentication authentication) {
        User user = authenticationService.getCurrentUser(authentication);
        Integer total = cartItemRepository.getTotalQuantityByUser(user.getId());
        return total != null ? total : 0;
    }

    public BigDecimal getCartTotal(Authentication authentication) {
        User user = authenticationService.getCurrentUser(authentication);
        BigDecimal total = cartItemRepository.getTotalValueByUser(user.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public String validateCart(Authentication authentication) {
        CartResponse cart = getCartForUser(authentication);

        if (cart.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Cart is empty");
        }

        // Additional validation logic can be added here
        return "Cart is valid";
    }
}
