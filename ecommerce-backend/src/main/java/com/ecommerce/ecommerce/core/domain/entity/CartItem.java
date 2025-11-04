package com.ecommerce.ecommerce.core.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * CartItem entity representing an item in a user's shopping cart.
 * Links users to products with specified quantities and variants.
 */
@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_user", columnList = "user_id"),
    @Index(name = "idx_cart_product", columnList = "product_id"),
    @Index(name = "idx_cart_variant", columnList = "product_variant_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant; // For size, color variants

    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculate total price for this cart item
     */
    public java.math.BigDecimal getTotalPrice() {
        if (productVariant != null) {
            return productVariant.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
        }
        return product.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
    }

    /**
     * Get the price to use for this cart item (variant price if exists, otherwise product price)
     */
    public java.math.BigDecimal getUnitPrice() {
        if (productVariant != null) {
            return productVariant.getPrice();
        }
        return product.getPrice();
    }

    /**
     * Check if this cart item has a product variant
     */
    public boolean hasVariant() {
        return productVariant != null;
    }

    /**
     * Get variant display name (size, color, etc.)
     */
    public String getVariantDisplayName() {
        if (productVariant != null) {
            return productVariant.getDisplayName();
        }
        return null;
    }
}
