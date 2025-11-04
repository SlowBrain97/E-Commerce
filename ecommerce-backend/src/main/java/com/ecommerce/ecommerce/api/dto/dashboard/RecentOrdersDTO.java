package com.ecommerce.ecommerce.api.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RecentOrdersDTO {
    private List<RecentOrderItemDTO> recentOrders;
    private int totalCount;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class RecentOrderItemDTO {
        private Long orderId;
        private String customerName;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime orderDate;
        private String paymentMethod;
    }
}
