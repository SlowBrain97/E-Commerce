package com.ecommerce.ecommerce.core.repository;

import com.ecommerce.ecommerce.core.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity operations.
 * Provides methods for order management, tracking, and analytics.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  /**
   * Find orders by user with pagination
   */
  Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  /**
   * Find orders by status with pagination
   */
  Page<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status, Pageable pageable);

  /**
   * Find orders by user and status
   */
  Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Order.OrderStatus status, Pageable pageable);

  /**
   * Find orders by order number
   */
  List<Order> findByOrderNumber(String orderNumber);

  /**
   * Find orders within date range
   */
  @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
  List<Order> findByDateRange(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

  /**
   * Find orders by payment status
   */
  Page<Order> findByPaymentStatusOrderByCreatedAtDesc(Order.PaymentStatus paymentStatus, Pageable pageable);

  /**
   * Find pending orders (for processing)
   */
  @Query(value = "SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED') ORDER BY o.createdAt ASC", nativeQuery = true)
  List<Order> findPendingOrders(Pageable pageable);

  /**
   * Find orders by total amount range
   */
  @Query("SELECT o FROM Order o WHERE o.totalAmount BETWEEN :minAmount AND :maxAmount")
  Page<Order> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount,
                                       @Param("maxAmount") BigDecimal maxAmount,
                                       Pageable pageable);

  /**
   * Count orders by status
   */
  long countByStatus(Order.OrderStatus status);

  /**
   * Count orders by payment status
   */
  long countByPaymentStatus(Order.PaymentStatus paymentStatus);

  /**
   * Get total sales amount for date range
   */
  @Query(value = "SELECT SUM(o.totalAmount) FROM Order o WHERE o.status NOT IN ('CANCELLED', 'REFUNDED') AND o.createdAt BETWEEN :startDate AND :endDate", nativeQuery = true)
  default BigDecimal getTotalSalesAmount(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate) {
    return null;
  }

  /**
   * Get order statistics by date
   */
  @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.createdAt >= :since GROUP BY o.status")
  List<Object[]> getOrderStatsByStatus(@Param("since") LocalDateTime since);

  /**
   * Find recent orders for user
   */
  @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
  List<Order> findRecentOrdersByUser(@Param("userId") Long userId, Pageable pageable);

  /**
   * Find orders requiring action (shipped but not delivered)
   */
  @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED' AND o.deliveredDate IS NULL AND o.shippedDate < :cutoffDate")
  List<Order> findOrdersRequiringAttention(@Param("cutoffDate") LocalDateTime cutoffDate);

  /**
   * Get total revenue from all completed orders
   */
  @Query(value = "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status NOT IN ('CANCELLED', 'REFUNDED')", nativeQuery = true)
  Optional<BigDecimal> getTotalRevenue();

  /**
   * Get revenue for a specific period
   */
  @Query(value = "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status NOT IN ('CANCELLED', 'REFUNDED') AND o.createdAt BETWEEN :startDate AND :endDate", nativeQuery = true)
  Optional<BigDecimal> getRevenueForPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  /**
   * Count orders for a specific period
   */
  @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
  Long countOrdersForPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  /**
   * Get average delivery time in days
   */
  @Query(value = "SELECT AVG(DATEDIFF(o.deliveredDate, o.createdAt)) FROM Order o WHERE o.deliveredDate IS NOT NULL", nativeQuery = true)
  Optional<Double> getAverageDeliveryTime();

  /**
   * Find top 10 recent orders
   */
  List<Order> findTop10ByOrderByCreatedAtDesc();

  /**
   * Get monthly sales data for dashboard
   */
  @Query(value = "SELECT DATE_TRUNC('month', o.created_at) as month, " +
         "COUNT(o) as order_count, " +
         "COALESCE(SUM(o.total_amount), 0) as total_revenue " +
         "FROM orders o " +
         "WHERE o.created_at >= :startDate AND o.status NOT IN ('CANCELLED', 'REFUNDED') " +
         "GROUP BY DATE_TRUNC('month', o.created_at) " +
         "ORDER BY month", nativeQuery = true)
  List<Object[]> getMonthlySalesData(@Param("startDate") LocalDateTime startDate);
}
