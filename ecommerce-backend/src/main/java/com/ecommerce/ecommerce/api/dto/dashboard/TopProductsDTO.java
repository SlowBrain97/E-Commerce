package com.ecommerce.ecommerce.api.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TopProductsDTO {
    private List<TopProductItemDTO> topProducts;
    private int totalCount;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class TopProductItemDTO {
        private Long productId;
        private String productName;
        private String categoryName;
        private Long totalSold;
        private BigDecimal totalRevenue;
        private String productImage;
        private BigDecimal averageRating;
    }
}
