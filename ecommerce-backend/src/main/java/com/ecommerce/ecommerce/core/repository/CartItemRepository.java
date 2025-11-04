package com.ecommerce.ecommerce.core.repository;

import com.ecommerce.ecommerce.core.domain.entity.CartItem;
import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity operations.
 * Provides methods for shopping cart management and persistence.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find all cart items for a user
     */
    List<CartItem> findByUserIdOrderByAddedAtDesc(Long userId);

    /**
     * Find cart item by user and product
     */
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    /**
     * Find cart item by user and product variant
     */
    Optional<CartItem> findByUserAndProductVariant(User user, com.ecommerce.ecommerce.core.domain.entity.ProductVariant productVariant);

    /**
     * Find cart item by user, product and variant
     */
    Optional<CartItem> findByUserAndProductAndProductVariant(User user, Product product, com.ecommerce.ecommerce.core.domain.entity.ProductVariant productVariant);

    /**
     * Count cart items for a user
     */
    long countByUserId(Long userId);

    /**
     * Check if product exists in user's cart
     */
    boolean existsByUserAndProduct(User user, Product product);

    /**
     * Check if product variant exists in user's cart
     */
    boolean existsByUserAndProductVariant(User user, com.ecommerce.ecommerce.core.domain.entity.ProductVariant productVariant);

    /**
     * Get total quantity of items in user's cart
     */
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.user.id = :userId")
    Integer getTotalQuantityByUser(@Param("userId") Long userId);

    /**
     * Get total value of items in user's cart
     */
    @Query("SELECT SUM(ci.quantity * CASE WHEN ci.productVariant IS NOT NULL THEN ci.productVariant.price ELSE ci.product.price END) " +
           "FROM CartItem ci WHERE ci.user.id = :userId")
    java.math.BigDecimal getTotalValueByUser(@Param("userId") Long userId);

    /**
     * Remove all cart items for a user
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId")
    void removeAllByUserId(@Param("userId") Long userId);

    /**
     * Remove cart item by user and product
     */
    @Modifying
    void deleteByUserAndProduct(User user, Product product);

    /**
     * Remove cart item by user and product variant
     */
    @Modifying
    void deleteByUserAndProductVariant(User user, com.ecommerce.ecommerce.core.domain.entity.ProductVariant productVariant);

    /**
     * Update quantity of cart item
     */
    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity WHERE ci.id = :cartItemId AND ci.user.id = :userId")
    int updateQuantity(@Param("cartItemId") Long cartItemId,
                      @Param("userId") Long userId,
                      @Param("quantity") Integer quantity);

    /**
     * Find old cart items for cleanup (older than specified days)
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.addedAt < :cutoffDate")
    List<CartItem> findOldCartItems(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Remove old cart items
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.addedAt < :cutoffDate")
    int removeOldCartItems(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Get cart items with low stock warning
     */
    @Query("SELECT ci FROM CartItem ci WHERE " +
           "(ci.productVariant IS NOT NULL AND ci.productVariant.stockQuantity <= :threshold) OR " +
           "(ci.productVariant IS NULL AND ci.product.stockQuantity <= :threshold)")
    List<CartItem> findCartItemsWithLowStock(@Param("threshold") Integer threshold);

    /**
     * Find cart items by product IDs for batch operations
     */
    List<CartItem> findByProductIdIn(List<Long> productIds);

    /**
     * Check if user has items in cart for specific products
     */
    @Query("SELECT COUNT(ci) > 0 FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id IN :productIds")
    boolean hasItemsInCart(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);

    List<CartItem> findByUserIdAndId(Long aLong, Long aLong1);
}
