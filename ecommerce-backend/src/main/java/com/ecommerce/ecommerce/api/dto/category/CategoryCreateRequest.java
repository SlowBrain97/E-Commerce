package com.ecommerce.ecommerce.api.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryCreateRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private String imageUrl;

    private String icon;

    private Boolean isActive = true;

    private Boolean isFeatured = false;

    private Integer sortOrder = 0;

    private Long parentId; // For subcategories

}
