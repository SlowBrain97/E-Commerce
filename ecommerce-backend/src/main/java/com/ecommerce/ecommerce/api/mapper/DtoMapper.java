package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.auth.UserInfo;
import com.ecommerce.ecommerce.api.dto.cart.AddToCartRequest;
import com.ecommerce.ecommerce.api.dto.cart.CartItemResponse;
import com.ecommerce.ecommerce.api.dto.cart.CartResponse;
import com.ecommerce.ecommerce.api.dto.cart.UpdateCartItemRequest;
import com.ecommerce.ecommerce.api.dto.category.CategoryCreateRequest;
import com.ecommerce.ecommerce.api.dto.category.CategoryResponse;
import com.ecommerce.ecommerce.api.dto.category.CategoryUpdateRequest;
import com.ecommerce.ecommerce.api.dto.common.PageResponse;
import com.ecommerce.ecommerce.api.dto.order.*;
import com.ecommerce.ecommerce.api.dto.product.ProductResponse;
import com.ecommerce.ecommerce.api.dto.product.ProductVariantDTO;
import com.ecommerce.ecommerce.api.dto.review.CreateReviewRequest;
import com.ecommerce.ecommerce.api.dto.review.ReviewResponse;
import com.ecommerce.ecommerce.api.dto.user.UpdateProfileRequest;
import com.ecommerce.ecommerce.api.dto.user.UserResponse;
import com.ecommerce.ecommerce.core.domain.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main mapper service that uses MapStruct mappers for entity-DTO conversion.
 * Provides a centralized facade for all mapping operations.
 */
@Component
@RequiredArgsConstructor
public class DtoMapper {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final CartItemMapper cartItemMapper;
    private final AuthMapper authMapper;
    private final CategoryMapper categoryMapper;

    // ==================== ENTITY TO DTO MAPPINGS ====================

    // Order mapping methods using MapStruct
    public Address toAddressEntity(AddressDTO addressDTO) {
        return addressMapper.addressDTOToAddress(addressDTO);
    }

    public AddressDTO toAddressDTO(Address address) {
        return addressMapper.addressToAddressDTO(address);
    }

    public OrderItem toOrderItemEntity(OrderItemRequest request, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setProductName(product.getName());
        orderItem.setProductSku(product.getSku());
        orderItem.setProductImageUrl(product.getPrimaryImageUrl());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setUnitPrice(product.getPrice());
        return orderItem;
    }

    public OrderItemResponse toOrderItemResponseDTO(OrderItem orderItem) {
        return orderMapper.orderItemsToDto(List.of(orderItem)).get(0);
    }

    public OrderResponse toOrderResponseDTO(Order order) {
        return orderMapper.orderToOrderResponse(order);
    }

    public Order createOrderFromRequest(CreateOrderRequest request, User user) {
        Order order = orderMapper.createOrderRequestToOrder(request);
        order.setUser(user);

        // Set addresses
        if (request.getShippingAddress() != null) {
            order.setShippingAddress(toAddressEntity(request.getShippingAddress()));
        }
        if (request.getBillingAddress() != null) {
            order.setBillingAddress(toAddressEntity(request.getBillingAddress()));
        } else if (request.getShippingAddress() != null) {
            order.setBillingAddress(toAddressEntity(request.getShippingAddress()));
        }

        // Set order items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<OrderItem> orderItems = request.getItems().stream()
                .map(itemRequest -> {
                    Product product = new Product(); // In real scenario, fetch from service
                    product.setId(itemRequest.getProductId());
                    return toOrderItemEntity(itemRequest, product);
                })
                .collect(Collectors.toList());
            order.setOrderItems(orderItems);
        }

        return order;
    }

    // User mapping methods using MapStruct
    public UserResponse toUserResponseDTO(User user) {
        return userMapper.userToUserResponse(user);
    }

    public void updateUserFromProfileRequest(UpdateProfileRequest request, User user) {
        userMapper.updateUserFromProfileRequest(request, user);
    }

    // Review mapping methods using MapStruct
    public Review toReviewEntity(CreateReviewRequest request, Product product, User user) {
        Review review = reviewMapper.createReviewRequestToReview(request);
        review.setProduct(product);
        review.setUser(user);
        return review;
    }

    public ReviewResponse toReviewResponseDTO(Review review) {
        return reviewMapper.reviewToReviewResponse(review);
    }

    // Product mapping methods using MapStruct
    public ProductResponse toProductResponseDTO(Product product) {
        return productMapper.productToProductResponse(product);
    }

    public Product productResponseToEntity(ProductResponse productResponse) {
        return productMapper.productResponseToProduct(productResponse);
    }

    public Product toProductEntity(com.ecommerce.ecommerce.api.dto.product.ProductCreateRequest request) {
        return productMapper.productCreateRequestToProduct(request);
    }

    public void updateProductFromRequest(com.ecommerce.ecommerce.api.dto.product.ProductUpdateRequest request, Product product) {
        productMapper.updateProductFromRequest(request, product);
    }

    public ProductVariant toProductVariantEntity(ProductVariantDTO variantDTO) {
        return productMapper.productVariantDtoToProductVariant(variantDTO);
    }

    public ProductVariantDTO toProductVariantDTO(ProductVariant variant) {
        return productMapper.productVariantToProductVariantDto(variant);
    }

    // Cart mapping methods using MapStruct
    public CartItemResponse toCartItemResponseDTO(CartItem cartItem) {
        return cartItemMapper.cartItemToCartItemResponse(cartItem);
    }

    public CartResponse toCartResponseDTO(List<CartItem> cartItems) {
        return cartItemMapper.cartItemsToCartResponse(cartItems);
    }

    public CartItem toCartItemEntity(AddToCartRequest request) {
        return cartItemMapper.addToCartRequestToCartItem(request);
    }

    public CartItem updateCartItemFromRequest(UpdateCartItemRequest request) {
        return cartItemMapper.updateCartItemRequestToCartItem(request);
    }

    // Auth mapping methods using MapStruct
    public UserInfo toUserInfoDTO(User user) {
        return authMapper.userToUserInfo(user);
    }

    // Category mapping methods using MapStruct
    public CategoryResponse toCategoryResponseDTO(Category category) {
        return categoryMapper.categoryToCategoryResponse(category);
    }

    public com.ecommerce.ecommerce.api.dto.product.ProductResponse.CategoryDTO toCategoryDTO(Category category) {
        return categoryMapper.categoryToCategoryDto(category);
    }

    public Category toCategoryEntity(CategoryCreateRequest request) {
        return categoryMapper.categoryCreateRequestToCategory(request);
    }

    public void updateCategoryFromRequest(CategoryUpdateRequest request, Category category) {
        categoryMapper.updateCategoryFromRequest(request, category);
    }

    public List<CategoryResponse> toCategoryResponseDTOs(List<Category> categories) {
        return categoryMapper.categoriesToCategoryResponses(categories);
    }

    // ==================== DTO TO ENTITY MAPPINGS (Reverse) ====================

    // Convert DTOs back to entities for update operations
    public User toUserEntity(UserResponse userResponse) {
        return userMapper.userResponseToUser(userResponse);
    }

    public Category toCategoryEntity(CategoryResponse categoryResponse) {
        return categoryMapper.categoryResponseToCategory(categoryResponse);
    }

    public Order toOrderEntity(OrderResponse orderResponse) {
        return orderMapper.orderResponseToOrder(orderResponse);
    }

    public Review toReviewEntity(ReviewResponse reviewResponse) {
        return reviewMapper.reviewResponseToReview(reviewResponse);
    }

    // ==================== LIST MAPPINGS ====================

    public List<OrderItemResponse> toOrderItemResponseDTOs(List<OrderItem> orderItems) {
        return orderMapper.orderItemsToDto(orderItems);
    }

    public List<OrderResponse> toOrderResponseDTOs(List<Order> orders) {
        return orderMapper.ordersToOrderResponses(orders);
    }

    public List<UserResponse> toUserResponseDTOs(List<User> users) {
        return userMapper.usersToUserResponses(users);
    }

    public List<ReviewResponse> toReviewResponseDTOs(List<Review> reviews) {
        return reviewMapper.reviewsToReviewResponses(reviews);
    }

    public List<ProductResponse> toProductResponseDTOs(List<Product> products) {
        return productMapper.productsToProductResponses(products);
    }

    public List<CategoryResponse> toCategoryResponseDTOsFromList(List<Category> categories) {
        return categories.stream()
                .map(this::toCategoryResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== BATCH OPERATIONS ====================

    public void updateUserFromDto(UserResponse userResponse, User user) {
        if (userResponse.getFirstName() != null) user.setFirstName(userResponse.getFirstName());
        if (userResponse.getLastName() != null) user.setLastName(userResponse.getLastName());
        if (userResponse.getEmail() != null) user.setEmail(userResponse.getEmail());
        if (userResponse.getPhoneNumber() != null) user.setPhoneNumber(userResponse.getPhoneNumber());
        if (userResponse.getAvatarUrl() != null) user.setAvatarUrl(userResponse.getAvatarUrl());
    }


    public void updateProductFromDto(ProductResponse productResponse, Product product) {
        if (productResponse.getName() != null) product.setName(productResponse.getName());
        if (productResponse.getDescription() != null) product.setDescription(productResponse.getDescription());
        if (productResponse.getShortDescription() != null) product.setShortDescription(productResponse.getShortDescription());
        if (productResponse.getPrice() != null) product.setPrice(productResponse.getPrice());
        if (productResponse.getCompareAtPrice() != null) product.setCompareAtPrice(productResponse.getCompareAtPrice());
        if (productResponse.getStockQuantity() != null) product.setStockQuantity(productResponse.getStockQuantity());
        if (productResponse.getIsActive() != null) product.setIsActive(productResponse.getIsActive());
        if (productResponse.getIsFeatured() != null) product.setIsFeatured(productResponse.getIsFeatured());
    }

    // ==================== UTILITY METHODS ====================

    public boolean hasChanges(UserResponse userResponse, User user) {
        return !user.getFirstName().equals(userResponse.getFirstName()) ||
               !user.getLastName().equals(userResponse.getLastName()) ||
               !user.getEmail().equals(userResponse.getEmail()) ||
               !user.getPhoneNumber().equals(userResponse.getPhoneNumber());
    }

    public boolean hasChanges(CategoryResponse categoryResponse, Category category) {
        return !category.getName().equals(categoryResponse.getName()) ||
               !category.getDescription().equals(categoryResponse.getDescription());
    }

    public boolean hasChanges(ProductResponse productResponse, Product product) {
        return !product.getName().equals(productResponse.getName()) ||
               !product.getDescription().equals(productResponse.getDescription()) ||
               !product.getPrice().equals(productResponse.getPrice());
    }

    // ==================== LEGACY METHODS FOR BACKWARD COMPATIBILITY ====================

    public OrderItemResponse convertToOrderItemResponseDTO(OrderItem orderItem) {
        return toOrderItemResponseDTO(orderItem);
    }

    public OrderResponse convertToOrderResponseDTO(Order order) {
        return toOrderResponseDTO(order);
    }

    public UserResponse convertToUserResponseDTO(User user) {
        return toUserResponseDTO(user);
    }

    public ReviewResponse convertToReviewResponseDTO(Review review) {
        return toReviewResponseDTO(review);
    }

    public com.ecommerce.ecommerce.api.dto.product.ProductResponse convertToProductResponseDTO(Product product) {
        return toProductResponseDTO(product);
    }

    public <E,D>PageResponse<D> toPageDto(Page<E> page, Function<E,D> mapper){
        List<D> dtoList = page.stream().map(mapper).toList();

        return PageResponse.<D>builder().data(dtoList)
                .page(page.getTotalPages())
                .size(page.getSize())
                .totalElements(page.getNumberOfElements())
                .isLastPage(page.isLast())
                .build();
    }
}
