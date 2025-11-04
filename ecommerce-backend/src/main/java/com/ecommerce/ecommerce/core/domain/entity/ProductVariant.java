package com.ecommerce.ecommerce.core.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ProductVariant entity representing different variations of a product.
 * Supports size, color, material, and other product variations.
 */
@Entity
@Table(name = "product_variants", indexes = {
    @Index(name = "idx_variant_product", columnList = "product_id"),
    @Index(name = "idx_variant_sku", columnList = "sku", unique = true),
    @Index(name = "idx_variant_type", columnList = "variant_type"),
    @Index(name = "idx_variant_active", columnList = "is_active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "variant_type", nullable = false) // size, color, material, etc.
    private String variantType;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "variant_value", nullable = false) // S, M, L, Red, Blue, etc.
    private String variantValue;

    @Size(max = 255)
    @Column(name = "variant_description")
    private String variantDescription;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @DecimalMin(value = "0.0")
    @Column(name = "compare_at_price", precision = 10, scale = 2)
    private BigDecimal compareAtPrice;

    @Min(0)
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Get display name for this variant (e.g., "Size: M", "Color: Red")
     */
    public String getDisplayName() {
        return variantType + ": " + variantValue;
    }

    /**
     * Get full display name with description if available
     */
    public String getFullDisplayName() {
        String displayName = getDisplayName();
        if (variantDescription != null && !variantDescription.trim().isEmpty()) {
            displayName += " (" + variantDescription + ")";
        }
        return displayName;
    }

    /**
     * Check if variant is in stock
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Check if variant is on sale
     */
    public boolean isOnSale() {
        return compareAtPrice != null && compareAtPrice.compareTo(price) > 0;
    }

    /**
     * Calculate discount percentage
     */
    public BigDecimal getDiscountPercentage() {
        if (!isOnSale()) {
            return BigDecimal.ZERO;
        }
        return compareAtPrice.subtract(price)
                .divide(compareAtPrice, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
