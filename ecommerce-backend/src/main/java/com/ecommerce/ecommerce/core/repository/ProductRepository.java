package com.ecommerce.ecommerce.core.repository;

import com.ecommerce.ecommerce.core.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity operations.
 * Provides methods for product catalog, search, filtering, and inventory management.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find active products with pagination
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);

    /**
     * Find featured products
     */
    List<Product> findByIsFeaturedTrueAndIsActiveTrue();

    /**
     * Find products by category
     */
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    /**
     * Find products by category slug
     */
    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.id = :categoryId AND p.isActive = true")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Search products by name or description
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Filter products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   Pageable pageable);

    /**
     * Find products in stock
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stockQuantity > 0")
    Page<Product> findInStockProducts(Pageable pageable);

    /**
     * Find products out of stock
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (p.stockQuantity IS NULL OR p.stockQuantity <= 0)")
    List<Product> findOutOfStockProducts();

    /**
     * Find products by tags
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%'))")
    Page<Product> findByTag(@Param("tag") String tag, Pageable pageable);

    /**
     * Find related products (same category, different product)
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId AND p.isActive = true")
    List<Product> findRelatedProducts(@Param("categoryId") Long categoryId,
                                    @Param("productId") Long productId,
                                    Pageable pageable);

    /**
     * Find products by multiple category IDs
     */
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.isActive = true")
    Page<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    /**
     * Advanced search with multiple filters
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false AND (p.stockQuantity IS NULL OR p.stockQuantity <= 0))) " +
           "AND (:searchTerm IS NULL OR " +
           "   LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "   LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> findWithFilters(@Param("categoryId") Long categoryId,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                @Param("inStock") Boolean inStock,
                                @Param("searchTerm") String searchTerm,
                                Pageable pageable);

    /**
     * Find products with low stock (less than threshold)
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stockQuantity <= :threshold AND p.stockQuantity > 0")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    /**
     * Count products by category
     */
    long countByCategoryIdAndIsActiveTrue(Long categoryId);

    /**
     * Find products for admin dashboard
     */
    @Query("SELECT p FROM Product p WHERE p.createdAt >= :since ORDER BY p.createdAt DESC")
    List<Product> findRecentlyAddedProducts(@Param("since") java.time.LocalDateTime since, Pageable pageable);

    /**
     * Count active products
     */
    Long countByIsActiveTrue();

    /**
     * Count products by stock quantity less than threshold
     */
    Long countByStockQuantityLessThan(Integer threshold);

    /**
     * Count products by exact stock quantity (for out of stock)
     */
    Long countByStockQuantity(Integer stockQuantity);

    /**
     * Find top selling product (simplified version)
     */
    @Query("SELECT p FROM Product p LEFT JOIN p.orderItems oi GROUP BY p ORDER BY COUNT(oi) DESC")
    Optional<Product> findTopSellingProduct();

    /**
     * Find top 10 products by total sold quantity
     */
    @Query(value = "SELECT p FROM Product p " +
           "LEFT JOIN order_items oi ON p.id = oi.product_id " +
           "LEFT JOIN orders o ON oi.order_id = o.id AND o.status NOT IN ('CANCELLED', 'REFUNDED') " +
           "GROUP BY p.id " +
           "ORDER BY COALESCE(SUM(oi.quantity), 0) DESC", nativeQuery = true)
    List<Product> findTop10ByOrderByTotalSoldDesc();

    /**
     * Find top selling products (by order count)
     */
    @Query(value = "SELECT p, SUM(oi.quantity) as totalSold FROM Product p " +
           "JOIN p.orderItems oi " +
           "JOIN oi.order o " +
           "WHERE o.status NOT IN ('CANCELLED', 'REFUNDED') " +
           "GROUP BY p " +
           "ORDER BY totalSold DESC",nativeQuery = true)
    List<Product> findTopSellingProducts(Pageable pageable);
}
