package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.api.dto.product.ProductResponse;
import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.ProductVariant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service class for inventory management operations.
 * Handles stock tracking, low stock alerts, and inventory reporting.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InventoryService {

    private final ProductService productService;
    private final ProductVariantService productVariantService;

    /**
     * Get inventory summary
     */
    public InventorySummary getInventorySummary() {
        List<Product> allProducts = productService.getAllActiveProducts(org.springframework.data.domain.Pageable.unpaged()).getContent();

        int totalProducts = allProducts.size();
        int inStockProducts = 0;
        int outOfStockProducts = 0;
        int lowStockProducts = 0;

        for (Product product : allProducts) {
            if (isProductInStock(product)) {
                inStockProducts++;
            } else {
                outOfStockProducts++;
            }

            if (isProductLowStock(product)) {
                lowStockProducts++;
            }
        }

        return InventorySummary.builder()
                .totalProducts(totalProducts)
                .inStockProducts(inStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .lowStockProducts(lowStockProducts)
                .build();
    }

    /**
     * Get low stock products
     */
    public List<Product> getLowStockProducts(Integer threshold) {
        return productService.getLowStockProducts(threshold);
    }

    /**
     * Get out of stock products
     */
    public List<Product> getOutOfStockProducts() {
        return productService.getOutOfStockProducts();
    }

    /**
     * Check if product is in stock
     */
    public boolean isProductInStock(Product product) {
        // Check if product has variants
        List<ProductVariant> variants = productVariantService.getVariantsByProduct(product.getId());

        if (variants.isEmpty()) {
            // Product without variants - check product stock
            return product.getStockQuantity() != null && product.getStockQuantity() > 0;
        } else {
            // Product with variants - check if any variant is in stock
            return variants.stream().anyMatch(ProductVariant::isInStock);
        }
    }

    /**
     * Check if product is low stock
     */
    public boolean isProductLowStock(Product product) {
        Integer threshold = getLowStockThreshold();

        // Check if product has variants
        List<ProductVariant> variants = productVariantService.getVariantsByProduct(product.getId());

        if (variants.isEmpty()) {
            // Product without variants
            Integer stock = product.getStockQuantity();
            return stock != null && stock > 0 && stock <= threshold;
        } else {
            // Product with variants - check if any variant is low stock
            return variants.stream()
                    .anyMatch(variant -> variant.getStockQuantity() != null &&
                           variant.getStockQuantity() > 0 &&
                           variant.getStockQuantity() <= threshold);
        }
    }

    /**
     * Update product stock
     */
    @Transactional
    public void updateProductStock(Long productId, Integer newStock) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productService.updateStock(productId, newStock);
        log.info("Product stock updated: {} -> {}", productId, newStock);
    }

    /**
     * Update variant stock
     */
    @Transactional
    public void updateVariantStock(Long variantId, Integer newStock) {
        ProductVariant variant = productVariantService.getVariantById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        productVariantService.updateStock(variantId, newStock);
        log.info("Variant stock updated: {} -> {}", variantId, newStock);
    }

    /**
     * Reserve stock for order
     */
    @Transactional
    public boolean reserveStock(Map<Long, Integer> productQuantities, Map<Long, Integer> variantQuantities) {
        log.info("Reserving stock for order");

        // Reserve product stock
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Integer currentStock = product.getStockQuantity();
            if (currentStock == null || currentStock < quantity) {
                log.error("Insufficient stock for product: {} - Required: {}, Available: {}",
                         productId, quantity, currentStock);
                return false;
            }

            updateProductStock(productId, currentStock - quantity);
        }

        // Reserve variant stock
        for (Map.Entry<Long, Integer> entry : variantQuantities.entrySet()) {
            Long variantId = entry.getKey();
            Integer quantity = entry.getValue();

            ProductVariant variant = productVariantService.getVariantById(variantId)
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            Integer currentStock = variant.getStockQuantity();
            if (currentStock == null || currentStock < quantity) {
                log.error("Insufficient stock for variant: {} - Required: {}, Available: {}",
                         variantId, quantity, currentStock);
                return false;
            }

            updateVariantStock(variantId, currentStock - quantity);
        }

        log.info("Stock reserved successfully");
        return true;
    }

    /**
     * Release reserved stock (for cancelled orders)
     */
    @Transactional
    public void releaseStock(Map<Long, Integer> productQuantities, Map<Long, Integer> variantQuantities) {
        log.info("Releasing reserved stock");

        // Release product stock
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Integer currentStock = product.getStockQuantity();
            updateProductStock(productId, currentStock != null ? currentStock + quantity : quantity);
        }

        // Release variant stock
        for (Map.Entry<Long, Integer> entry : variantQuantities.entrySet()) {
            Long variantId = entry.getKey();
            Integer quantity = entry.getValue();

            ProductVariant variant = productVariantService.getVariantById(variantId)
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            Integer currentStock = variant.getStockQuantity();
            updateVariantStock(variantId, currentStock != null ? currentStock + quantity : quantity);
        }

        log.info("Stock released successfully");
    }

    /**
     * Get stock report for admin dashboard
     */
    public StockReport getStockReport() {
        List<Product> lowStockProducts = getLowStockProducts(getLowStockThreshold());
        List<Product> outOfStockProducts = getOutOfStockProducts();

        return StockReport.builder()
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .lowStockCount(lowStockProducts.size())
                .outOfStockCount(outOfStockProducts.size())
                .build();
    }

    /**
     * Check if order can be fulfilled
     */
    public boolean canFulfillOrder(Map<Long, Integer> productQuantities, Map<Long, Integer> variantQuantities) {
        // Check product stock
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer requiredQuantity = entry.getValue();

            Product product = productService.getProductById(productId).orElse(null);
            if (product == null) {
                return false;
            }

            Integer availableStock = product.getStockQuantity();
            if (availableStock == null || availableStock < requiredQuantity) {
                return false;
            }
        }

        // Check variant stock
        for (Map.Entry<Long, Integer> entry : variantQuantities.entrySet()) {
            Long variantId = entry.getKey();
            Integer requiredQuantity = entry.getValue();

            ProductVariant variant = productVariantService.getVariantById(variantId).orElse(null);
            if (variant == null) {
                return false;
            }

            Integer availableStock = variant.getStockQuantity();
            if (availableStock == null || availableStock < requiredQuantity) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get low stock threshold (configurable)
     */
    private Integer getLowStockThreshold() {
        // This could be configurable via application properties
        return 10;
    }

    /**
     * DTO for inventory summary
     */
    public static class InventorySummary {
        private int totalProducts;
        private int inStockProducts;
        private int outOfStockProducts;
        private int lowStockProducts;

        public static InventorySummaryBuilder builder() {
            return new InventorySummaryBuilder();
        }

        // Getters and setters
        public int getTotalProducts() { return totalProducts; }
        public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
        public int getInStockProducts() { return inStockProducts; }
        public void setInStockProducts(int inStockProducts) { this.inStockProducts = inStockProducts; }
        public int getOutOfStockProducts() { return outOfStockProducts; }
        public void setOutOfStockProducts(int outOfStockProducts) { this.outOfStockProducts = outOfStockProducts; }
        public int getLowStockProducts() { return lowStockProducts; }
        public void setLowStockProducts(int lowStockProducts) { this.lowStockProducts = lowStockProducts; }

        public static class InventorySummaryBuilder {
            private InventorySummary summary = new InventorySummary();

            public InventorySummaryBuilder totalProducts(int totalProducts) {
                summary.totalProducts = totalProducts;
                return this;
            }

            public InventorySummaryBuilder inStockProducts(int inStockProducts) {
                summary.inStockProducts = inStockProducts;
                return this;
            }

            public InventorySummaryBuilder outOfStockProducts(int outOfStockProducts) {
                summary.outOfStockProducts = outOfStockProducts;
                return this;
            }

            public InventorySummaryBuilder lowStockProducts(int lowStockProducts) {
                summary.lowStockProducts = lowStockProducts;
                return this;
            }

            public InventorySummary build() {
                return summary;
            }
        }
    }

    /**
     * DTO for stock report
     */
    public static class StockReport {
        private List<Product> lowStockProducts;
        private List<Product> outOfStockProducts;
        private int lowStockCount;
        private int outOfStockCount;

        public static StockReportBuilder builder() {
            return new StockReportBuilder();
        }

        // Getters and setters
        public List<Product> getLowStockProducts() { return lowStockProducts; }
        public void setLowStockProducts(List<Product> lowStockProducts) { this.lowStockProducts = lowStockProducts; }
        public List<Product> getOutOfStockProducts() { return outOfStockProducts; }
        public void setOutOfStockProducts(List<Product> outOfStockProducts) { this.outOfStockProducts = outOfStockProducts; }
        public int getLowStockCount() { return lowStockCount; }
        public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }
        public int getOutOfStockCount() { return outOfStockCount; }
        public void setOutOfStockCount(int outOfStockCount) { this.outOfStockCount = outOfStockCount; }

        public static class StockReportBuilder {
            private StockReport report = new StockReport();

            public StockReportBuilder lowStockProducts(List<Product> lowStockProducts) {
                report.lowStockProducts = lowStockProducts;
                return this;
            }

            public StockReportBuilder outOfStockProducts(List<Product> outOfStockProducts) {
                report.outOfStockProducts = outOfStockProducts;
                return this;
            }

            public StockReportBuilder lowStockCount(int lowStockCount) {
                report.lowStockCount = lowStockCount;
                return this;
            }

            public StockReportBuilder outOfStockCount(int outOfStockCount) {
                report.outOfStockCount = outOfStockCount;
                return this;
            }

            public StockReport build() {
                return report;
            }
        }
    }
}
