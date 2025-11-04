package com.ecommerce.ecommerce.api.controller;

import com.ecommerce.ecommerce.api.dto.category.CategoryCreateRequest;
import com.ecommerce.ecommerce.api.dto.category.CategoryResponse;
import com.ecommerce.ecommerce.api.dto.category.CategoryUpdateRequest;
import com.ecommerce.ecommerce.api.dto.common.ApiResponse;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.Category;
import com.ecommerce.ecommerce.core.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final DtoMapper dtoMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        Category category = categoryService.createCategory(request);
        CategoryResponse response = dtoMapper.toCategoryResponseDTO(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Category created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        CategoryResponse response = dtoMapper.toCategoryResponseDTO(category);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category retrieved successfully", response));
    }

    @GetMapping("/main-categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getMainCategories() {
        List<Category> categories = categoryService.getRootCategories();
        List<CategoryResponse> response = dtoMapper.toCategoryResponseDTOsFromList(categories);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Main categories retrieved successfully", response));
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubcategories(@PathVariable Long parentId) {
        List<Category> categories = categoryService.getSubcategoriesByParentId(parentId);
        List<CategoryResponse> response = dtoMapper.toCategoryResponseDTOsFromList(categories);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Subcategories retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        Category category = categoryService.updateCategory(id, request);
        CategoryResponse response = dtoMapper.toCategoryResponseDTO(category);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category deleted successfully", "Category deleted successfully"));
    }

    @PutMapping("/{id}/featured")
    public ResponseEntity<ApiResponse<CategoryResponse>> setFeatured(
            @PathVariable Long id,
            @RequestParam boolean featured) {
        Category category = categoryService.setFeatured(id, featured);
        CategoryResponse response = dtoMapper.toCategoryResponseDTO(category);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category featured status updated successfully", response));
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<ApiResponse<CategoryResponse>> setActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        Category category = categoryService.setActive(id, active);
        CategoryResponse response = dtoMapper.toCategoryResponseDTO(category);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category active status updated successfully", response));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getFeaturedCategories() {
        List<Category> categories = categoryService.getFeaturedCategories();
        List<CategoryResponse> response = dtoMapper.toCategoryResponseDTOsFromList(categories);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Featured categories retrieved successfully", response));
    }

    @GetMapping("/hierarchy")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategoriesInHierarchy() {
        List<Category> categories = categoryService.getAllCategoriesInHierarchy();
        List<CategoryResponse> response = dtoMapper.toCategoryResponseDTOsFromList(categories);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category hierarchy retrieved successfully", response));
    }

    @GetMapping("/with-products")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoriesWithProducts() {
        List<Category> categories = categoryService.getCategoriesWithProducts();
        List<CategoryResponse> response = dtoMapper.toCategoryResponseDTOsFromList(categories);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Categories with products retrieved successfully", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategories(@RequestParam String q) {
        List<Category> categories = categoryService.searchCategories(q);
        List<CategoryResponse> response = dtoMapper.toCategoryResponseDTOsFromList(categories);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category search completed successfully", response));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategoryStats() {
        Map<String, Object> response = Map.of(
            "totalCategories", categoryService.getCategoryStats().getTotalCategories(),
            "activeCategories", categoryService.getCategoryStats().getActiveCategories(),
            "featuredCategories", categoryService.getCategoryStats().getFeaturedCategories(),
            "rootCategories", categoryService.getCategoryStats().getRootCategories()
        );
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category statistics retrieved successfully", response));
    }
}
