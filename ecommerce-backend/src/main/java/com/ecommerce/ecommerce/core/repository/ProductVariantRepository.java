package com.ecommerce.ecommerce.core.repository;

import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.ProductVariant;
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
 * Repository interface for ProductVariant entity operations.
 * Provides methods for product variant management and inventory tracking.
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    /**
     * Find variants by product ordered by sort order
     */
    List<ProductVariant> findByProductIdOrderBySortOrder(Long productId);

    /**
     * Find active variants by product
     */
    List<ProductVariant> findByProductIdAndIsActiveTrueOrderBySortOrder(Long productId);

    /**
     * Find variant by SKU
     */
    Optional<ProductVariant> findBySku(String sku);

    /**
     * Find variants by type and value
     */
    List<ProductVariant> findByVariantTypeAndVariantValue(String variantType, String variantValue);

    /**
     * Find variants by product and type
     */
    List<ProductVariant> findByProductAndVariantTypeOrderBySortOrder(Product product, String variantType);

    /**
     * Find variants in stock
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true AND pv.stockQuantity > 0")
    List<ProductVariant> findInStockVariantsByProduct(@Param("productId") Long productId);

    /**
     * Find low stock variants
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.isActive = true AND pv.stockQuantity > 0 AND pv.stockQuantity <= :threshold")
    List<ProductVariant> findLowStockVariants(@Param("threshold") Integer threshold);

    /**
     * Find out of stock variants
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.isActive = true AND (pv.stockQuantity IS NULL OR pv.stockQuantity <= 0)")
    List<ProductVariant> findOutOfStockVariants();

    /**
     * Search variants by value
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.isActive = true AND " +
           "LOWER(pv.variantValue) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ProductVariant> searchVariants(@Param("searchTerm") String searchTerm);

    /**
     * Find variants by price range
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.isActive = true AND pv.price BETWEEN :minPrice AND :maxPrice")
    List<ProductVariant> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find variants on sale
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.isActive = true AND pv.compareAtPrice IS NOT NULL AND pv.compareAtPrice > pv.price")
    List<ProductVariant> findSaleVariants();

    /**
     * Get variant statistics by type
     */
    @Query("SELECT pv.variantType, COUNT(pv) FROM ProductVariant pv WHERE pv.isActive = true GROUP BY pv.variantType")
    List<Object[]> getVariantStatsByType();

    /**
     * Count variants by product
     */
    long countByProductIdAndIsActiveTrue(Long productId);

    /**
     * Count variants by type
     */
    long countByVariantTypeAndIsActiveTrue(String variantType);

    /**
     * Get total stock for a product across all variants
     */
    @Query("SELECT SUM(pv.stockQuantity) FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true")
    Integer getTotalStockByProduct(@Param("productId") Long productId);

    /**
     * Find variants with specific type for multiple products
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id IN :productIds AND pv.variantType = :variantType AND pv.isActive = true")
    List<ProductVariant> findVariantsByProductIdsAndType(@Param("productIds") List<Long> productIds,
                                                       @Param("variantType") String variantType);

    /**
     * Get unique variant types for a product
     */
    @Query("SELECT DISTINCT pv.variantType FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true ORDER BY pv.variantType")
    List<String> findDistinctVariantTypesByProduct(@Param("productId") Long productId);

    /**
     * Get variant values for a specific type and product
     */
    @Query("SELECT pv.variantValue FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.variantType = :variantType AND pv.isActive = true ORDER BY pv.sortOrder")
    List<String> findVariantValuesByTypeAndProduct(@Param("productId") Long productId, @Param("variantType") String variantType);

    /**
     * Find cheapest variant for a product
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true ORDER BY pv.price ASC")
    List<ProductVariant> findCheapestVariantsByProduct(@Param("productId") Long productId, Pageable pageable);

    /**
     * Find most expensive variant for a product
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true ORDER BY pv.price DESC")
    List<ProductVariant> findMostExpensiveVariantsByProduct(@Param("productId") Long productId, Pageable pageable);

    /**
     * Update stock for a variant
     */
    @Query("UPDATE ProductVariant pv SET pv.stockQuantity = :stockQuantity WHERE pv.id = :variantId")
    void updateStock(@Param("variantId") Long variantId, @Param("stockQuantity") Integer stockQuantity);

    /**
     * Find variants requiring restock (below threshold)
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.isActive = true AND pv.stockQuantity <= :threshold ORDER BY pv.stockQuantity ASC")
    List<ProductVariant> findVariantsNeedingRestock(@Param("threshold") Integer threshold);

    /**
     * Check if variant exists for product and type
     */
    @Query("SELECT COUNT(pv) > 0 FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.variantType = :variantType AND pv.variantValue = :variantValue")
    boolean variantExists(@Param("productId") Long productId, @Param("variantType") String variantType, @Param("variantValue") String variantValue);

    /**
     * Get variants for admin inventory management
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.isActive = true ORDER BY pv.product.id, pv.variantType, pv.variantValue")
    Page<ProductVariant> findAllForAdmin(Pageable pageable);

  List<ProductVariant> findByProductIdAndVariantTypeOrderBySortOrder(Long productId, String variantType);
}
