package com.ecommerce.ecommerce.api.dto.dashboard;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductStatsDTO {
    private Long totalProducts;
    private Long activeProducts;
    private Long inactiveProducts;
    private Long lowStockProducts;
    private Long outOfStockProducts;
    private Long totalCategories;
    private Long topSellingProductId;
    private String topSellingProductName;
    private Long topSellingProductSales;
}
