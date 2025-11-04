package com.ecommerce.ecommerce.api.dto.dashboard;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderStatsDTO {
    private Long totalOrders;
    private Long pendingOrders;
    private Long processingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private Long returnedOrders;
    private Double orderCompletionRate;
    private Double averageDeliveryTime;
}
