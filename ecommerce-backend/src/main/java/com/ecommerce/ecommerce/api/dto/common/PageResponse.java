package com.ecommerce.ecommerce.api.dto.common;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
  private List<T> data;
  private int size;
  private int page;
  private int totalElements;
  private int totalPages;
  private boolean isLastPage;
}
