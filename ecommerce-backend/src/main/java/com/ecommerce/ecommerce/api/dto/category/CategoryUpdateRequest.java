package com.ecommerce.ecommerce.api.dto.category;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryUpdateRequest {

    private String name;

    private String description;

    private String imageUrl;

    private String icon;

    private Boolean isActive;

    private Boolean isFeatured;

    private Integer sortOrder;

    private Long parentId; // For moving categories

}
