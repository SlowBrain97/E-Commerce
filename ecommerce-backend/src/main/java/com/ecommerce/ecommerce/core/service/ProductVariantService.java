package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.ProductVariant;
import com.ecommerce.ecommerce.core.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class for ProductVariant entity operations.
 * Handles product variant management, inventory tracking, and variant-specific operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    /**
     * Create a new product variant
     */
    @Transactional
    public ProductVariant createVariant(Product product, String sku, String variantType,
                                      String variantValue, String variantDescription,
                                      BigDecimal price, BigDecimal compareAtPrice,
                                      Integer stockQuantity) {
        log.info("Creating variant for product {}: {} - {}", product.getId(), variantType, variantValue);

        // Check if variant already exists
        if (productVariantRepository.variantExists(product.getId(), variantType, variantValue)) {
            throw new RuntimeException("Variant already exists for this product and type");
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(sku)
                .variantType(variantType)
                .variantValue(variantValue)
                .variantDescription(variantDescription)
                .price(price)
                .compareAtPrice(compareAtPrice)
                .stockQuantity(stockQuantity)
                .isActive(true)
                .sortOrder(getNextSortOrder(product.getId(), variantType))
                .build();

        ProductVariant savedVariant = productVariantRepository.save(variant);
        log.info("Variant created: {}", savedVariant.getId());

        return savedVariant;
    }

    /**
     * Get variants by product
     */
    public List<ProductVariant> getVariantsByProduct(Long productId) {
        return productVariantRepository.findByProductIdAndIsActiveTrueOrderBySortOrder(productId);
    }

    /**
     * Get variant by ID
     */
    public Optional<ProductVariant> getVariantById(Long id) {
        return productVariantRepository.findById(id);
    }

    /**
     * Get active variant by ID
     */
    public Optional<ProductVariant> getActiveVariantById(Long id) {
        return productVariantRepository.findById(id)
                .filter(ProductVariant::getIsActive);
    }

    /**
     * Get variant by SKU
     */
    public Optional<ProductVariant> getVariantBySku(String sku) {
        return productVariantRepository.findBySku(sku);
    }

    /**
     * Update variant
     */
    @Transactional
    public ProductVariant updateVariant(Long variantId, String variantDescription,
                                      BigDecimal price, BigDecimal compareAtPrice,
                                      Integer stockQuantity) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        variant.setVariantDescription(variantDescription);
        variant.setPrice(price);
        variant.setCompareAtPrice(compareAtPrice);
        variant.setStockQuantity(stockQuantity);

        ProductVariant savedVariant = productVariantRepository.save(variant);
        log.info("Variant updated: {}", variantId);

        return savedVariant;
    }

    /**
     * Update variant stock
     */
    @Transactional
    public ProductVariant updateStock(Long variantId, Integer stockQuantity) {
        productVariantRepository.updateStock(variantId, stockQuantity);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        log.info("Variant stock updated: {} -> {}", variantId, stockQuantity);
        return variant;
    }

    /**
     * Activate/deactivate variant
     */
    @Transactional
    public ProductVariant setActive(Long variantId, boolean active) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        variant.setIsActive(active);
        ProductVariant savedVariant = productVariantRepository.save(variant);

        log.info("Variant {} active status: {}", variantId, active);
        return savedVariant;
    }

    /**
     * Update sort order
     */
    @Transactional
    public void updateSortOrder(Long variantId, Integer sortOrder) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        variant.setSortOrder(sortOrder);
        productVariantRepository.save(variant);

        log.info("Variant sort order updated: {} -> {}", variantId, sortOrder);
    }

    /**
     * Delete variant (soft delete)
     */
    @Transactional
    public void deleteVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        variant.setIsActive(false);
        productVariantRepository.save(variant);

        log.info("Variant deleted: {}", variantId);
    }

    /**
     * Get variants by type for a product
     */
    public List<ProductVariant> getVariantsByType(Long productId, String variantType) {
        return productVariantRepository.findByProductIdAndVariantTypeOrderBySortOrder(productId, variantType);
    }

    /**
     * Get in-stock variants for a product
     */
    public List<ProductVariant> getInStockVariants(Long productId) {
        return productVariantRepository.findInStockVariantsByProduct(productId);
    }

    /**
     * Get low stock variants
     */
    public List<ProductVariant> getLowStockVariants(Integer threshold) {
        return productVariantRepository.findLowStockVariants(threshold);
    }

    /**
     * Get out of stock variants
     */
    public List<ProductVariant> getOutOfStockVariants() {
        return productVariantRepository.findOutOfStockVariants();
    }

    /**
     * Search variants by value
     */
    public List<ProductVariant> searchVariants(String searchTerm) {
        return productVariantRepository.searchVariants(searchTerm);
    }

    /**
     * Get variants by price range
     */
    public List<ProductVariant> getVariantsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productVariantRepository.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Get sale variants
     */
    public List<ProductVariant> getSaleVariants() {
        return productVariantRepository.findSaleVariants();
    }

    /**
     * Get unique variant types for a product
     */
    public List<String> getVariantTypesForProduct(Long productId) {
        return productVariantRepository.findDistinctVariantTypesByProduct(productId);
    }

    /**
     * Get variant values for a specific type and product
     */
    public List<String> getVariantValuesForType(Long productId, String variantType) {
        return productVariantRepository.findVariantValuesByTypeAndProduct(productId, variantType);
    }

    /**
     * Get total stock for a product across all variants
     */
    public Integer getTotalStockForProduct(Long productId) {
        Integer totalStock = productVariantRepository.getTotalStockByProduct(productId);
        return totalStock != null ? totalStock : 0;
    }

    /**
     * Check if variant is in stock
     */
    public boolean isInStock(Long variantId) {
        Optional<ProductVariant> variant = getVariantById(variantId);
        return variant.map(ProductVariant::isInStock).orElse(false);
    }

    /**
     * Check if variant is on sale
     */
    public boolean isOnSale(Long variantId) {
        Optional<ProductVariant> variant = getVariantById(variantId);
        return variant.map(ProductVariant::isOnSale).orElse(false);
    }

    /**
     * Get variant statistics by type
     */
    public List<Object[]> getVariantStatsByType() {
        return productVariantRepository.getVariantStatsByType();
    }

    /**
     * Get variants needing restock
     */
    public List<ProductVariant> getVariantsNeedingRestock(Integer threshold) {
        return productVariantRepository.findVariantsNeedingRestock(threshold);
    }

    /**
     * Count variants for a product
     */
    public long countVariantsForProduct(Long productId) {
        return productVariantRepository.countByProductIdAndIsActiveTrue(productId);
    }

    /**
     * Get next sort order for a variant type
     */
    private Integer getNextSortOrder(Long productId, String variantType) {
        List<ProductVariant> variants = productVariantRepository.findByProductIdAndVariantTypeOrderBySortOrder(productId, variantType);

        if (variants.isEmpty()) {
            return 1;
        }

        return variants.get(variants.size() - 1).getSortOrder() + 1;
    }

    /**
     * DTO for variant statistics
     */
    public static class VariantStats {
        private long totalVariants;
        private long inStockVariants;
        private long outOfStockVariants;
        private long saleVariants;

        public static VariantStatsBuilder builder() {
            return new VariantStatsBuilder();
        }

        // Getters and setters
        public long getTotalVariants() { return totalVariants; }
        public void setTotalVariants(long totalVariants) { this.totalVariants = totalVariants; }
        public long getInStockVariants() { return inStockVariants; }
        public void setInStockVariants(long inStockVariants) { this.inStockVariants = inStockVariants; }
        public long getOutOfStockVariants() { return outOfStockVariants; }
        public void setOutOfStockVariants(long outOfStockVariants) { this.outOfStockVariants = outOfStockVariants; }
        public long getSaleVariants() { return saleVariants; }
        public void setSaleVariants(long saleVariants) { this.saleVariants = saleVariants; }

        public static class VariantStatsBuilder {
            private VariantStats variantStats = new VariantStats();

            public VariantStatsBuilder totalVariants(long totalVariants) {
                variantStats.totalVariants = totalVariants;
                return this;
            }

            public VariantStatsBuilder inStockVariants(long inStockVariants) {
                variantStats.inStockVariants = inStockVariants;
                return this;
            }

            public VariantStatsBuilder outOfStockVariants(long outOfStockVariants) {
                variantStats.outOfStockVariants = outOfStockVariants;
                return this;
            }

            public VariantStatsBuilder saleVariants(long saleVariants) {
                variantStats.saleVariants = saleVariants;
                return this;
            }

            public VariantStats build() {
                return variantStats;
            }
        }
    }
}
