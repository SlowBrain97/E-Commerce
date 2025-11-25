package com.ecommerce.ecommerce.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for product search and filtering
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

  private String searchTerm;
  private Long categoryId;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private Boolean inStock;
  @Builder.Default
  private String sortBy = "createdAt"; // name, price, createdAt, rating
  @Builder.Default
  private String sortDirection = "desc"; // asc, desc
  @Builder.Default
  private Integer page = 0;
  @Builder.Default
  private Integer size = 20;
}
