package com.ecommerce.ecommerce.api.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for product creation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

  @NotBlank(message = "Product name is required")
  @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
  private String name;

  @Size(max = 5000, message = "Description cannot exceed 5000 characters")
  private String description;

  @Size(max = 1000, message = "Short description cannot exceed 1000 characters")
  private String shortDescription;

  @NotBlank(message = "SKU is required")
  @Size(min = 3, max = 100, message = "SKU must be between 3 and 100 characters")
  private String sku;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
  @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 fractional digits")
  private BigDecimal price;

  @DecimalMin(value = "0.0", message = "Compare at price cannot be negative")
  @Digits(integer = 10, fraction = 2, message = "Compare at price must have at most 10 integer digits and 2 fractional digits")
  private BigDecimal compareAtPrice;

  @Min(value = 0, message = "Stock quantity cannot be negative")
  private Integer stockQuantity;

  @NotNull(message = "Category ID is required")
  private Long categoryId;

  @Size(max = 500, message = "Tags cannot exceed 500 characters")
  private String tags;

  @Min(value = 0, message = "Weight cannot be negative")
  private BigDecimal weight;

  @Size(max = 255, message = "Dimensions cannot exceed 255 characters")
  private String dimensions;

  private List<ProductImageDTO> images;
  private List<ProductVariantDTO> variants;
}
