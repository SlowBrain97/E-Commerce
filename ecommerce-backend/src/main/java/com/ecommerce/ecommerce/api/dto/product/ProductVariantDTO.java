package com.ecommerce.ecommerce.api.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for product variant information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {

  private Long id;

  @NotBlank(message = "Variant type is required")
  @Size(min = 2, max = 50, message = "Variant type must be between 2 and 50 characters")
  private String variantType;

  @NotBlank(message = "Variant value is required")
  @Size(min = 2, max = 100, message = "Variant value must be between 2 and 100 characters")
  private String variantValue;

  @Size(max = 255, message = "Variant description cannot exceed 255 characters")
  private String variantDescription;

  @NotNull(message = "Variant price is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Variant price must be greater than 0")
  @Digits(integer = 10, fraction = 2, message = "Variant price must have at most 10 integer digits and 2 fractional digits")
  private BigDecimal price;

  @DecimalMin(value = "0.0", message = "Variant compare at price cannot be negative")
  @Digits(integer = 10, fraction = 2, message = "Variant compare at price must have at most 10 integer digits and 2 fractional digits")
  private BigDecimal compareAtPrice;

  @Min(value = 0, message = "Variant stock quantity cannot be negative")
  private Integer stockQuantity;

  private Integer sortOrder;
}
