package com.ecommerce.ecommerce.api.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DashboardOverviewDTO {
    private SalesStatsDTO salesStats;
    private ProductStatsDTO productStats;
    private UserStatsDTO userStats;
    private OrderStatsDTO orderStats;
    private RecentOrdersDTO recentOrders;
    private TopProductsDTO topProducts;
}
