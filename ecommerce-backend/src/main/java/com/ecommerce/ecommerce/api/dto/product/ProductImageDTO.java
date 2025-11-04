package com.ecommerce.ecommerce.api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for product image information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {

  @NotBlank(message = "Image URL is required")
  private String imageUrl;

  @Size(max = 255, message = "Alt text cannot exceed 255 characters")
  private String altText;

  private Boolean isPrimary = false;
  private Integer sortOrder;
}
