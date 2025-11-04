package com.ecommerce.ecommerce.api.controller;

import com.ecommerce.ecommerce.api.dto.common.ApiResponse;
import com.ecommerce.ecommerce.api.dto.dashboard.DashboardOverviewDTO;
import com.ecommerce.ecommerce.core.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Dashboard operations.
 * Provides endpoints for retrieving statistical data for admin dashboard.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get dashboard overview with all statistics
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardOverviewDTO>> getDashboardOverview() {
        log.info("Getting dashboard overview data");

        try {
            DashboardOverviewDTO overview = dashboardService.getDashboardOverview();
            return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Dashboard overview retrieved successfully",
                overview
            ));
        } catch (Exception e) {
            log.error("Error retrieving dashboard overview", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving dashboard overview: " + e.getMessage(),
                    "/api/dashboard/overview",
                    null
                ));
        }
    }

    /**
     * Get sales statistics
     */
    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<com.ecommerce.ecommerce.api.dto.dashboard.SalesStatsDTO>> getSalesStats() {
        log.info("Getting sales statistics");

        try {
            var salesStats = dashboardService.getSalesStats();
            return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Sales statistics retrieved successfully",
                salesStats
            ));
        } catch (Exception e) {
            log.error("Error retrieving sales statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving sales statistics: " + e.getMessage(),
                    "/api/dashboard/sales",
                    null
                ));
        }
    }

    /**
     * Get product statistics
     */
    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<com.ecommerce.ecommerce.api.dto.dashboard.ProductStatsDTO>> getProductStats() {
        log.info("Getting product statistics");

        try {
            var productStats = dashboardService.getProductStats();
            return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Product statistics retrieved successfully",
                productStats
            ));
        } catch (Exception e) {
            log.error("Error retrieving product statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving product statistics: " + e.getMessage(),
                    "/api/dashboard/products",
                    null
                ));
        }
    }

    /**
     * Get user statistics
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<com.ecommerce.ecommerce.api.dto.dashboard.UserStatsDTO>> getUserStats() {
        log.info("Getting user statistics");

        try {
            var userStats = dashboardService.getUserStats();
            return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "User statistics retrieved successfully",
                userStats
            ));
        } catch (Exception e) {
            log.error("Error retrieving user statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving user statistics: " + e.getMessage(),
                    "/api/dashboard/users",
                    null
                ));
        }
    }

    /**
     * Get order statistics
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<com.ecommerce.ecommerce.api.dto.dashboard.OrderStatsDTO>> getOrderStats() {
        log.info("Getting order statistics");

        try {
            var orderStats = dashboardService.getOrderStats();
            return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Order statistics retrieved successfully",
                orderStats
            ));
        } catch (Exception e) {
            log.error("Error retrieving order statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving order statistics: " + e.getMessage(),
                    "/api/dashboard/orders",
                    null
                ));
        }
    }

    /**
     * Get recent orders
     */
    @GetMapping("/recent-orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<com.ecommerce.ecommerce.api.dto.dashboard.RecentOrdersDTO>> getRecentOrders() {
        log.info("Getting recent orders");

        try {
            var recentOrders = dashboardService.getRecentOrders();
            return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Recent orders retrieved successfully",
                recentOrders
            ));
        } catch (Exception e) {
            log.error("Error retrieving recent orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving recent orders: " + e.getMessage(),
                    "/api/dashboard/recent-orders",
                    null
                ));
        }
    }


    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<com.ecommerce.ecommerce.api.dto.dashboard.TopProductsDTO>> getTopProducts() {
        log.info("Getting top selling products");

        try {
            var topProducts = dashboardService.getTopProducts();
            return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Top products retrieved successfully",
                topProducts
            ));
        } catch (Exception e) {
            log.error("Error retrieving top products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error retrieving top products: " + e.getMessage(),
                    "/api/dashboard/top-products",
                    null
                ));
        }
    }
}
