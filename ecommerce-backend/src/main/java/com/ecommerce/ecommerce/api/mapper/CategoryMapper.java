package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.category.CategoryCreateRequest;
import com.ecommerce.ecommerce.api.dto.category.CategoryResponse;
import com.ecommerce.ecommerce.api.dto.category.CategoryUpdateRequest;
import com.ecommerce.ecommerce.api.dto.product.ProductResponse;
import com.ecommerce.ecommerce.core.domain.entity.Category;
import com.ecommerce.ecommerce.api.mapper.config.MapperConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper interface for Category entity and DTOs.
 * Uses MapStruct to automatically generate implementation.
 */
@Mapper(config = MapperConfiguration.class)
public interface CategoryMapper {

    // Category to CategoryResponse mapping
    @Mapping(target = "parentId", expression = "java(category.getParent() != null ? category.getParent().getId() : null)")
    @Mapping(target = "subcategoryCount", expression = "java(category.getSubcategories() != null ? category.getSubcategories().size() : 0)")
    CategoryResponse categoryToCategoryResponse(Category category);

    // Category to CategoryDTO mapping (for ProductResponse)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    ProductResponse.CategoryDTO categoryToCategoryDto(Category category);

    // CategoryResponse to Category entity mapping (reverse)
    @Mapping(target = "parent", ignore = true) // Will be set by service if parentId provided
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category categoryResponseToCategory(CategoryResponse categoryResponse);

    // CategoryCreateRequest to Category entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true) // Will be set by service if parentId provided
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category categoryCreateRequestToCategory(CategoryCreateRequest request);

    // CategoryUpdateRequest to Category entity mapping (for updating existing category)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true) // Will be set by service if parentId provided
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateCategoryFromRequest(CategoryUpdateRequest request, @MappingTarget Category category);

    // List mapping for batch operations
    @Mapping(target = "parentId", expression = "java(category.getParent() != null ? category.getParent().getId() : null)")
    @Mapping(target = "subcategoryCount", expression = "java(category.getSubcategories() != null ? category.getSubcategories().size() : 0)")
    List<CategoryResponse> categoriesToCategoryResponses(List<Category> categories);

}
