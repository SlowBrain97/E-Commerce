package com.ecommerce.ecommerce.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for product response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

  private Long id;
  private String name;
  private String description;
  private String shortDescription;
  private String sku;
  private BigDecimal price;
  private BigDecimal compareAtPrice;
  private Integer stockQuantity;
  private Boolean isActive;
  private Boolean isFeatured;
  private BigDecimal weight;
  private String dimensions;
  private String tags;
  private Double averageRating;
  private Integer reviewCount;
  private String primaryImageUrl;
  private List<String> allImageUrls;
  private CategoryDTO category;
  private List<ProductVariantDTO> variants;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategoryDTO {
    private Long id;
    private String name;
    private String description;
  }
}
