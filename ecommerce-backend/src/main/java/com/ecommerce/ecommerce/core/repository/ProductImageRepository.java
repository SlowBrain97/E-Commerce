package com.ecommerce.ecommerce.core.repository;

import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ProductImage entity operations.
 * Provides methods for product image management and queries.
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * Find images by product ordered by sort order
     */
    List<ProductImage> findByProductIdOrderBySortOrder(Long productId);

    /**
     * Find primary image by product
     */
    Optional<ProductImage> findByProductAndIsPrimaryTrue(Product product);

    /**
     * Find primary image by product ID
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Long productId);

    /**
     * Count images by product
     */
    long countByProductId(Long productId);

    /**
     * Find all primary images for multiple products
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id IN :productIds AND pi.isPrimary = true")
    List<ProductImage> findPrimaryImagesByProductIds(@Param("productIds") List<Long> productIds);

    /**
     * Set all images as non-primary for a product
     */
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.id = :productId")
    void clearPrimaryImage(@Param("productId") Long productId);

    /**
     * Set specific image as primary
     */
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = true WHERE pi.id = :imageId AND pi.product.id = :productId")
    void setPrimaryImage(@Param("imageId") Long imageId, @Param("productId") Long productId);

    /**
     * Delete images by product ID
     */
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    /**
     * Find images for products in a category
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.category.id = :categoryId AND pi.isPrimary = true")
    List<ProductImage> findPrimaryImagesByCategory(@Param("categoryId") Long categoryId);

    /**
     * Update sort order for images
     */
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.sortOrder = :sortOrder WHERE pi.id = :imageId")
    void updateSortOrder(@Param("imageId") Long imageId, @Param("sortOrder") Integer sortOrder);

    /**
     * Find images without alt text (for SEO optimization)
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.altText IS NULL OR pi.altText = ''")
    List<ProductImage> findImagesWithoutAltText();

    /**
     * Check if product has images
     */
    @Query("SELECT COUNT(pi) > 0 FROM ProductImage pi WHERE pi.product.id = :productId")
    boolean hasImages(@Param("productId") Long productId);
}
