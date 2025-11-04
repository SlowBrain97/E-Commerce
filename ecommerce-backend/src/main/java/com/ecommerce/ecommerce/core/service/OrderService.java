package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.core.domain.entity.Order;
import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.core.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Order entity operations.
 * Handles order management, status updates, and analytics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Create a new order
     */
    @Transactional
    public Order createOrder(Order order) {
        log.info("Creating order for user: {}", order.getUser().getId());

        // Generate order number
        String orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", savedOrder.getOrderNumber());

        return savedOrder;
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Get orders by user with pagination
     */
    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get orders by status
     */
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    /**
     * Update order status
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        // Set additional timestamps based on status
        switch (newStatus) {
            case SHIPPED:
                order.setShippedDate(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredDate(LocalDateTime.now());
                break;
            case PAID:
                order.setPaymentStatus(Order.PaymentStatus.PAID);
                order.setPaymentDate(LocalDateTime.now());
                break;
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order {} status updated: {} -> {}", orderId, oldStatus, newStatus);

        return savedOrder;
    }

    /**
     * Cancel order
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled in current status");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        log.info("Order cancelled: {}", orderId);
        return savedOrder;
    }

    /**
     * Get order statistics
     */
    public OrderStats getOrderStats() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long completedOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);

        return OrderStats.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .build();
    }

    /**
     * Get sales analytics for date range
     */
    public SalesAnalytics getSalesAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalSales = orderRepository.getTotalSalesAmount(startDate, endDate);
        List<Object[]> orderStats = orderRepository.getOrderStatsByStatus(startDate);
        List<Object[]> monthlyData = orderRepository.getMonthlySalesData(startDate);

        return SalesAnalytics.builder()
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .orderStats(orderStats)
                .monthlyData(monthlyData)
                .build();
    }

    /**
     * Find orders requiring attention
     */
    public List<Order> getOrdersRequiringAttention() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        return orderRepository.findOrdersRequiringAttention(cutoffDate);
    }

    /**
     * Generate unique order number
     */
    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String prefix = "ORD";
        String timestamp = String.format("%04d%02d%02d%02d%02d",
                now.getYear() % 10000,
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute());

        // Add random suffix to ensure uniqueness
        String suffix = String.format("%04d", (int) (Math.random() * 10000));

        return prefix + "-" + timestamp + suffix;
    }

    /**
     * DTO for order statistics
     */
    public static class OrderStats {
        private long totalOrders;
        private long pendingOrders;
        private long completedOrders;
        private long cancelledOrders;

        public static OrderStatsBuilder builder() {
            return new OrderStatsBuilder();
        }

        // Getters and setters
        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
        public long getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(long completedOrders) { this.completedOrders = completedOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }

        public static class OrderStatsBuilder {
            private OrderStats orderStats = new OrderStats();

            public OrderStatsBuilder totalOrders(long totalOrders) {
                orderStats.totalOrders = totalOrders;
                return this;
            }

            public OrderStatsBuilder pendingOrders(long pendingOrders) {
                orderStats.pendingOrders = pendingOrders;
                return this;
            }

            public OrderStatsBuilder completedOrders(long completedOrders) {
                orderStats.completedOrders = completedOrders;
                return this;
            }

            public OrderStatsBuilder cancelledOrders(long cancelledOrders) {
                orderStats.cancelledOrders = cancelledOrders;
                return this;
            }

            public OrderStats build() {
                return orderStats;
            }
        }
    }

    /**
     * DTO for sales analytics
     */
    public static class SalesAnalytics {
        private BigDecimal totalSales;
        private List<Object[]> orderStats;
        private List<Object[]> monthlyData;

        public static SalesAnalyticsBuilder builder() {
            return new SalesAnalyticsBuilder();
        }

        // Getters and setters
        public BigDecimal getTotalSales() { return totalSales; }
        public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
        public List<Object[]> getOrderStats() { return orderStats; }
        public void setOrderStats(List<Object[]> orderStats) { this.orderStats = orderStats; }
        public List<Object[]> getMonthlyData() { return monthlyData; }
        public void setMonthlyData(List<Object[]> monthlyData) { this.monthlyData = monthlyData; }

        public static class SalesAnalyticsBuilder {
            private SalesAnalytics salesAnalytics = new SalesAnalytics();

            public SalesAnalyticsBuilder totalSales(BigDecimal totalSales) {
                salesAnalytics.totalSales = totalSales;
                return this;
            }

            public SalesAnalyticsBuilder orderStats(List<Object[]> orderStats) {
                salesAnalytics.orderStats = orderStats;
                return this;
            }

            public SalesAnalyticsBuilder monthlyData(List<Object[]> monthlyData) {
                salesAnalytics.monthlyData = monthlyData;
                return this;
            }

            public SalesAnalytics build() {
                return salesAnalytics;
            }
        }
    }
}
