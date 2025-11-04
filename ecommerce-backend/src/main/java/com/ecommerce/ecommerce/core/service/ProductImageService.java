package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.ProductImage;
import com.ecommerce.ecommerce.core.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for ProductImage entity operations.
 * Handles product image management, primary image designation, and organization.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    /**
     * Add image to product
     */
    @Transactional
    public ProductImage addImageToProduct(Product product, String imageUrl, String altText, Boolean isPrimary) {
        log.info("Adding image to product: {}", product.getId());

        // If this is set as primary, clear existing primary image
        if (Boolean.TRUE.equals(isPrimary)) {
            productImageRepository.clearPrimaryImage(product.getId());
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .altText(altText)
                .isPrimary(isPrimary)
                .sortOrder(getNextSortOrder(product.getId()))
                .build();

        ProductImage savedImage = productImageRepository.save(image);
        log.info("Image added to product {}: {}", product.getId(), savedImage.getId());

        return savedImage;
    }

    /**
     * Get images by product
     */
    public List<ProductImage> getImagesByProduct(Long productId) {
        return productImageRepository.findByProductIdOrderBySortOrder(productId);
    }

    /**
     * Get primary image by product
     */
    public Optional<ProductImage> getPrimaryImageByProduct(Long productId) {
        return productImageRepository.findPrimaryImageByProductId(productId);
    }

    /**
     * Set primary image
     */
    @Transactional
    public ProductImage setPrimaryImage(Long imageId, Long productId) {
        log.info("Setting primary image for product {}: {}", productId, imageId);

        // Clear existing primary image
        productImageRepository.clearPrimaryImage(productId);

        // Set new primary image
        productImageRepository.setPrimaryImage(imageId, productId);

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        log.info("Primary image set for product {}: {}", productId, imageId);
        return image;
    }

    /**
     * Update image details
     */
    @Transactional
    public ProductImage updateImage(Long imageId, String imageUrl, String altText, Integer sortOrder) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        image.setImageUrl(imageUrl);
        image.setAltText(altText);
        if (sortOrder != null) {
            image.setSortOrder(sortOrder);
        }

        ProductImage savedImage = productImageRepository.save(image);
        log.info("Image updated: {}", imageId);

        return savedImage;
    }

    /**
     * Update sort order
     */
    @Transactional
    public void updateSortOrder(Long imageId, Integer sortOrder) {
        productImageRepository.updateSortOrder(imageId, sortOrder);
        log.info("Image sort order updated: {} -> {}", imageId, sortOrder);
    }

    /**
     * Delete image
     */
    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        productImageRepository.delete(image);
        log.info("Image deleted: {}", imageId);
    }

    /**
     * Delete all images for product
     */
    @Transactional
    public void deleteImagesByProduct(Long productId) {
        productImageRepository.deleteByProductId(productId);
        log.info("All images deleted for product: {}", productId);
    }

    /**
     * Get primary images for multiple products
     */
    public List<ProductImage> getPrimaryImagesForProducts(List<Long> productIds) {
        return productImageRepository.findPrimaryImagesByProductIds(productIds);
    }

    /**
     * Count images for product
     */
    public long countImagesForProduct(Long productId) {
        return productImageRepository.countByProductId(productId);
    }

    /**
     * Check if product has images
     */
    public boolean hasImages(Long productId) {
        return productImageRepository.hasImages(productId);
    }

    /**
     * Get images without alt text (for SEO)
     */
    public List<ProductImage> getImagesWithoutAltText() {
        return productImageRepository.findImagesWithoutAltText();
    }

    /**
     * Get primary images by category
     */
    public List<ProductImage> getPrimaryImagesByCategory(Long categoryId) {
        return productImageRepository.findPrimaryImagesByCategory(categoryId);
    }

    /**
     * Reorder images for a product
     */
    @Transactional
    public void reorderImages(List<Long> imageIds) {
        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            productImageRepository.updateSortOrder(imageId, i + 1);
        }
        log.info("Images reordered for {} images", imageIds.size());
    }

    /**
     * Get next sort order for a product
     */
    private Integer getNextSortOrder(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrder(productId);

        if (images.isEmpty()) {
            return 1;
        }

        return images.get(images.size() - 1).getSortOrder() + 1;
    }

    /**
     * Validate image URL format
     */
    public boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        // Basic URL validation - can be enhanced
        return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/");
    }

    /**
     * Get image statistics
     */
    public ImageStats getImageStats() {
        long totalImages = productImageRepository.count();
        long imagesWithoutAlt = productImageRepository.findImagesWithoutAltText().size();

        return ImageStats.builder()
                .totalImages(totalImages)
                .imagesWithoutAlt(imagesWithoutAlt)
                .build();
    }

    /**
     * DTO for image statistics
     */
    public static class ImageStats {
        private long totalImages;
        private long imagesWithoutAlt;

        public static ImageStatsBuilder builder() {
            return new ImageStatsBuilder();
        }

        // Getters and setters
        public long getTotalImages() { return totalImages; }
        public void setTotalImages(long totalImages) { this.totalImages = totalImages; }
        public long getImagesWithoutAlt() { return imagesWithoutAlt; }
        public void setImagesWithoutAlt(long imagesWithoutAlt) { this.imagesWithoutAlt = imagesWithoutAlt; }

        public static class ImageStatsBuilder {
            private ImageStats imageStats = new ImageStats();

            public ImageStatsBuilder totalImages(long totalImages) {
                imageStats.totalImages = totalImages;
                return this;
            }

            public ImageStatsBuilder imagesWithoutAlt(long imagesWithoutAlt) {
                imageStats.imagesWithoutAlt = imagesWithoutAlt;
                return this;
            }

            public ImageStats build() {
                return imageStats;
            }
        }
    }
}
