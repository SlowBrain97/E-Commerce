package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.api.dto.dashboard.*;
import com.ecommerce.ecommerce.core.domain.entity.Order;
import com.ecommerce.ecommerce.core.domain.entity.OrderItem;
import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Service class for Dashboard operations.
 * Provides statistical data for admin dashboard.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Get dashboard overview with all statistics
     */
    public DashboardOverviewDTO getDashboardOverview() {
        log.info("Getting dashboard overview data");

        return DashboardOverviewDTO.builder()
                .salesStats(getSalesStats())
                .productStats(getProductStats())
                .userStats(getUserStats())
                .orderStats(getOrderStats())
                .recentOrders(getRecentOrders())
                .topProducts(getTopProducts())
                .build();
    }

    /**
     * Get sales statistics
     */
    public SalesStatsDTO getSalesStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate startOfMonth = today.withDayOfMonth(1);

        // Total revenue and orders
        BigDecimal totalRevenue = orderRepository.getTotalRevenue().orElse(BigDecimal.ZERO);
        long totalOrders = orderRepository.count();

        // Monthly stats
        BigDecimal monthlyRevenue = orderRepository.getRevenueForPeriod(startOfMonth.atStartOfDay(), now)
                .orElse(BigDecimal.ZERO);
        Long monthlyOrders = orderRepository.countOrdersForPeriod(startOfMonth.atStartOfDay(), now);

        // Weekly stats
        BigDecimal weeklyRevenue = orderRepository.getRevenueForPeriod(startOfWeek.atStartOfDay(), now)
                .orElse(BigDecimal.ZERO);
        Long weeklyOrders = orderRepository.countOrdersForPeriod(startOfWeek.atStartOfDay(), now);

        // Daily stats
        BigDecimal dailyRevenue = orderRepository.getRevenueForPeriod(today.atStartOfDay(), now)
                .orElse(BigDecimal.ZERO);
        Long dailyOrders = orderRepository.countOrdersForPeriod(today.atStartOfDay(), now);

        // Calculate averages and growth rates
        BigDecimal averageOrderValue = totalOrders > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        // Calculate growth rates (simplified - compare with previous period)
        Double revenueGrowth = calculateGrowthRate(monthlyRevenue.doubleValue(),getPreviousMonthRevenue().doubleValue());
        Double orderGrowth = calculateGrowthRate(monthlyOrders.doubleValue(), getPreviousMonthOrders().doubleValue());

        return SalesStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .monthlyRevenue(monthlyRevenue)
                .weeklyRevenue(weeklyRevenue)
                .dailyRevenue(dailyRevenue)
                .totalOrders(totalOrders)
                .monthlyOrders(monthlyOrders)
                .weeklyOrders(weeklyOrders)
                .dailyOrders(dailyOrders)
                .averageOrderValue(averageOrderValue)
                .revenueGrowthPercentage(revenueGrowth)
                .orderGrowthPercentage(orderGrowth)
                .build();
    }
    /**
     * Get product statistics
     */
    public ProductStatsDTO getProductStats() {
        Long totalProducts = productRepository.count();
        Long activeProducts = productRepository.countByIsActiveTrue();
        Long inactiveProducts = totalProducts - activeProducts;
        Long lowStockProducts = productRepository.countByStockQuantityLessThan(10);
        Long outOfStockProducts = productRepository.countByStockQuantity(0);
        Long totalCategories = categoryRepository.count();

        // Get top selling product (simplified)
        var topProduct = productRepository.findTopSellingProduct();
        Long topSellingProductId = topProduct.map(Product::getId).orElse(null);
        String topSellingProductName = topProduct.map(Product::getName).orElse("N/A");
        Long topSellingProductSales = topProduct.map(p -> p.getOrderItems().stream()
                .mapToLong(OrderItem::getQuantity).sum()).orElse(0L);

        return ProductStatsDTO.builder()
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .inactiveProducts(inactiveProducts)
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .totalCategories(totalCategories)
                .topSellingProductId(topSellingProductId)
                .topSellingProductName(topSellingProductName)
                .topSellingProductSales(topSellingProductSales)
                .build();
    }

    /**
     * Get user statistics
     */
    public UserStatsDTO getUserStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate startOfMonth = today.withDayOfMonth(1);

        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countByIsVerifiedTrue();
        Long inactiveUsers = totalUsers - activeUsers;

        Long newUsersToday = userRepository.countByCreatedAtBetween(today.atStartOfDay(), now);
        Long newUsersThisWeek = userRepository.countByCreatedAtBetween(startOfWeek.atStartOfDay(), now);
        Long newUsersThisMonth = userRepository.countByCreatedAtBetween(startOfMonth.atStartOfDay(), now);

        Long totalAdmins = userRepository.countByRole("ADMIN");
        Long totalCustomers = userRepository.countByRole("CUSTOMER");

        Double userGrowth = calculateGrowthRate(Double.valueOf(newUsersThisMonth), Double.valueOf(getPreviousMonthNewUsers()));

        return UserStatsDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .newUsersToday(newUsersToday)
                .newUsersThisWeek(newUsersThisWeek)
                .newUsersThisMonth(newUsersThisMonth)
                .userGrowthPercentage(userGrowth)
                .totalAdmins(totalAdmins)
                .totalCustomers(totalCustomers)
                .build();
    }

    /**
     * Get order statistics
     */
    public OrderStatsDTO getOrderStats() {
        Long totalOrders = orderRepository.count();
        Long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.valueOf("PENDING"));
        Long processingOrders = orderRepository.countByStatus(Order.OrderStatus.valueOf("PROCESSING"));
        Long shippedOrders = orderRepository.countByStatus(Order.OrderStatus.valueOf("SHIPPED"));
        Long deliveredOrders = orderRepository.countByStatus(Order.OrderStatus.valueOf("DELIVERED"));
        Long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.valueOf("CANCELLED"));
        Long returnedOrders = orderRepository.countByStatus(Order.OrderStatus.valueOf("RETURNED"));

        // Calculate completion rate
        Long completedOrders = deliveredOrders;
        Double orderCompletionRate = totalOrders > 0 ?
                (double) completedOrders / totalOrders * 100 : 0.0;

        // Calculate average delivery time (simplified)
        Double averageDeliveryTime = orderRepository.getAverageDeliveryTime().orElse(0.0);

        return OrderStatsDTO.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .processingOrders(processingOrders)
                .shippedOrders(shippedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .returnedOrders(returnedOrders)
                .orderCompletionRate(orderCompletionRate)
                .averageDeliveryTime(averageDeliveryTime)
                .build();
    }

    /**
     * Get recent orders
     */
    public RecentOrdersDTO getRecentOrders() {
        var recentOrders = orderRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(order -> RecentOrdersDTO.RecentOrderItemDTO.builder()
                        .orderId(order.getId())
                        .customerName(order.getUser().getFirstName() + " " + order.getUser().getLastName())
                        .totalAmount(order.getTotalAmount())
                        .status(String.valueOf(order.getStatus()))
                        .orderDate(order.getCreatedAt())
                        .paymentMethod(order.getPaymentMethod())
                        .build())
                .collect(Collectors.toList());

        return RecentOrdersDTO.builder()
                .recentOrders(recentOrders)
                .totalCount(recentOrders.size())
                .build();
    }

    /**
     * Get top selling products
     */
    public TopProductsDTO getTopProducts() {
        var topProducts = productRepository.findTopSellingProducts(org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .map(product -> TopProductsDTO.TopProductItemDTO.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .categoryName(product.getCategory() != null ? product.getCategory().getName() : "N/A")
                        .totalSold(product.getOrderItems().stream().mapToLong(oi -> oi.getQuantity()).sum())
                        .totalRevenue(product.getPrice().multiply(BigDecimal.valueOf(
                                product.getOrderItems().stream().mapToLong(oi -> oi.getQuantity()).sum())))
                        .productImage(product.getImages() != null && !product.getImages().isEmpty() ?
                                product.getImages().get(0).getImageUrl() : null)
                        .averageRating(BigDecimal.valueOf(calculateAverageRating(product.getId())))
                        .build())
                .collect(Collectors.toList());

        return TopProductsDTO.builder()
                .topProducts(topProducts)
                .totalCount(topProducts.size())
                .build();
    }

    // Helper methods for growth calculations
    private Double calculateGrowthRate(Double current, Double previous) {
        if (previous == null || previous == 0) {
            return current != null && current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100;
    }

    private BigDecimal getPreviousMonthRevenue() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate firstDayThisMonth = now.toLocalDate().withDayOfMonth(1);
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDate lastDayLastMonth = firstDayThisMonth.minusDays(1);

        return orderRepository.getRevenueForPeriod(firstDayLastMonth.atStartOfDay(), lastDayLastMonth.atTime(23, 59, 59))
                .orElse(BigDecimal.ZERO);
    }

    private Long getPreviousMonthOrders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate firstDayThisMonth = now.toLocalDate().withDayOfMonth(1);
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDate lastDayLastMonth = firstDayThisMonth.minusDays(1);

        return orderRepository.countOrdersForPeriod(firstDayLastMonth.atStartOfDay(), lastDayLastMonth.atTime(23, 59, 59));
    }

    private Long getPreviousMonthNewUsers() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate firstDayThisMonth = now.toLocalDate().withDayOfMonth(1);
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDate lastDayLastMonth = firstDayThisMonth.minusDays(1);

        return userRepository.countByCreatedAtBetween(firstDayLastMonth.atStartOfDay(), lastDayLastMonth.atTime(23, 59, 59));
    }

    private Double calculateAverageRating(Long productId) {
        return reviewRepository.findAverageRatingByProductId(productId).orElse(0.0);
    }
}
