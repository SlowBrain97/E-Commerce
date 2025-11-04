package com.ecommerce.ecommerce.api.dto.category;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String icon;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer sortOrder;
    private Long parentId;
    private Integer subcategoryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
