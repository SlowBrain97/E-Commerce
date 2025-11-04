package com.ecommerce.ecommerce.api.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SalesStatsDTO {
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal dailyRevenue;
    private Long totalOrders;
    private Long monthlyOrders;
    private Long weeklyOrders;
    private Long dailyOrders;
    private BigDecimal averageOrderValue;
    private Double revenueGrowthPercentage;
    private Double orderGrowthPercentage;
}
