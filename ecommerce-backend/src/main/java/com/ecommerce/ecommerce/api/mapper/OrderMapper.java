package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.order.*;
import com.ecommerce.ecommerce.core.domain.entity.Order;
import com.ecommerce.ecommerce.core.domain.entity.OrderItem;
import com.ecommerce.ecommerce.api.mapper.config.MapperConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper interface for Order entity and DTOs.
 * Uses MapStruct to automatically generate implementation.
 */
@Mapper(config = MapperConfiguration.class, uses = {AddressMapper.class})
public interface OrderMapper {

    // Order to OrderResponse mapping


    // OrderItem to OrderItemResponse mapping
    @Named("orderItemsToDto")
    @Mapping(target = "id", expression = "java(orderItem.getId().toString())")
    @Mapping(target = "productId", expression = "java(orderItem.getProduct().getId().toString())")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "productSku", source = "productSku")
    @Mapping(target = "productImageUrl", source = "productImageUrl")
    @Mapping(target = "productVariantId", expression = "java(orderItem.getProductVariant() != null ? orderItem.getProductVariant().getId().toString() : null)")
    @Mapping(target = "variantName", source = "variantName")
    @Mapping(target = "totalPrice", expression = "java(orderItem.getTotalPrice())")
    List<OrderItemResponse> orderItemsToDto(List<OrderItem> orderItems);


    @Mapping(target = "id", expression = "java(order.getId().toString())")
    @Mapping(target = "status", expression = "java(order.getStatus().toString())")
    @Mapping(target = "paymentStatus", expression = "java(order.getPaymentStatus() != null ? order.getPaymentStatus().toString() : null)")
    @Mapping(target = "items", source = "orderItems", qualifiedByName = "orderItemsToDto")
    OrderResponse orderToOrderResponse(Order order);
    // OrderResponse to Order entity mapping (reverse)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Will be set in service
    @Mapping(target = "orderItems", ignore = true) // Will be set in service
    @Mapping(target = "status", ignore = true) // Will be set based on business logic
    @Mapping(target = "paymentStatus", ignore = true) // Will be set based on business logic
    @Mapping(target = "shippingAddress", ignore = true) // Will be set in service
    @Mapping(target = "billingAddress", ignore = true) // Will be set in service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order orderResponseToOrder(OrderResponse orderResponse);

    // List mapping for batch operations
    @Mapping(target = "id", expression = "java(order.getId().toString())")
    @Mapping(target = "status", expression = "java(order.getStatus().toString())")
    @Mapping(target = "paymentStatus", expression = "java(order.getPaymentStatus() != null ? order.getPaymentStatus().toString() : null)")
    @Mapping(target = "items", source = "orderItems", qualifiedByName = "orderItemsToDto")
    List<OrderResponse> ordersToOrderResponses(List<Order> orders);

    // CreateOrderRequest to Order entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Will be set in service
    @Mapping(target = "orderItems", ignore = true) // Will be set in service
    @Mapping(target = "orderNumber", ignore = true) // Will be generated
    @Mapping(target = "status", ignore = true) // Will be set to PENDING
    @Mapping(target = "paymentStatus", ignore = true) // Will be set to PENDING
    @Mapping(target = "subtotal", ignore = true) // Will be calculated
    @Mapping(target = "taxAmount", ignore = true) // Will be calculated
    @Mapping(target = "shippingAmount", ignore = true) // Will be calculated
    @Mapping(target = "discountAmount", ignore = true) // Will be calculated
    @Mapping(target = "totalAmount", ignore = true) // Will be calculated
    @Mapping(target = "currency", constant = "USD")
    @Mapping(target = "paymentIntentId", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "trackingNumber", ignore = true)
    @Mapping(target = "shippedDate", ignore = true)
    @Mapping(target = "deliveredDate", ignore = true)
    @Mapping(target = "couponCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order createOrderRequestToOrder(CreateOrderRequest request);

}
