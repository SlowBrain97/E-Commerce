package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.api.dto.category.CategoryCreateRequest;
import com.ecommerce.ecommerce.api.dto.category.CategoryUpdateRequest;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.Category;
import com.ecommerce.ecommerce.core.exception.BusinessException;
import com.ecommerce.ecommerce.core.exception.ErrorCode;
import com.ecommerce.ecommerce.core.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.core.repository.CategoryRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Category entity operations.
 * Handles category management, hierarchical operations, and navigation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DtoMapper mapper;
    /**
     * Create a new category
     */
    @Transactional
    public Category createCategory(CategoryCreateRequest request) {
        log.info("Creating category: {}", request.getName());

        // Check if category name already exists
        if (categoryRepository.findByNameAndIsActiveTrue(request.getName()).isPresent()) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_EXISTS, "Category with name " + request.getName() + " already exists");
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Parent category", request.getParentId()));
        }

        Category category = mapper.toCategoryEntity(request);
        category.setParent(parent);
        category.setSortOrder(getNextSortOrder(parent));

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: {}", savedCategory.getId());

        return savedCategory;
    }

    /**
     * Get category by ID
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * Get active category by ID
     */
    public Category getActiveCategoryById(Long id) {
        return categoryRepository.findById(id)
                .filter(Category::getIsActive)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * Get category by name
     */
    public Category getCategoryByName(String name) {
        return categoryRepository.findByNameAndIsActiveTrue(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * Get root categories (top level)
     */


    /**
     * Get subcategories by parent ID
     */
    public List<Category> getSubcategoriesByParentId(Long parentId) {
        return categoryRepository.findSubcategoriesByParentId(parentId);
    }

    /**
     * Get subcategories by parent
     */
    public List<Category> getSubcategories(Category parent) {
        return categoryRepository.findByParentAndIsActiveTrueOrderBySortOrder(parent);
    }

    /**
     * Get all categories in hierarchy order
     */
    public List<Category> getAllCategoriesInHierarchy() {
        return categoryRepository.findAllInHierarchyOrder();
    }

    /**
     * Get featured categories
     */
    public List<Category> getFeaturedCategories() {
        return categoryRepository.findByIsFeaturedTrueAndIsActiveTrueOrderBySortOrder();
    }

    /**
     * Search categories by name
     */
    public List<Category> searchCategories(String searchTerm) {
        return categoryRepository.searchCategories(searchTerm);
    }

    /**
     * Get categories with products
     */
    public List<Category> getCategoriesWithProducts() {
        return categoryRepository.findCategoriesWithProducts();
    }

    /**
     * Update category
     */
    @Transactional
    public Category updateCategory(Long categoryId, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Category", categoryId));

        mapper.updateCategoryFromRequest(request, category);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category updated: {}", categoryId);
        return savedCategory;
    }

    /**
     * Feature/unfeature category
     */
    @Transactional
    public Category setFeatured(Long categoryId, boolean featured) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Category", categoryId));

        category.setIsFeatured(featured);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category {} featured status: {}", categoryId, featured);
        return savedCategory;
    }

    /**
     * Activate/deactivate category
     */
    @Transactional
    public Category setActive(Long categoryId, boolean active) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Category", categoryId));

        category.setIsActive(active);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category {} active status: {}", categoryId, active);
        return savedCategory;
    }

    /**
     * Delete category (soft delete)
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, "Category", categoryId));

        category.setIsActive(false);
        categoryRepository.save(category);

        log.info("Category deleted: {}", categoryId);
    }

    /**
     * Get category path (breadcrumb)
     */
    public List<Object[]> getCategoryPath(Long categoryId) {
        List<Object[]> path = new ArrayList<>();

        Category category = categoryRepository.findCategoryForPath(categoryId).orElse(null);
        if (category == null) {
            return path;
        }

        // Build path recursively
        buildCategoryPath(category, path);
        return path;
    }

    /**
     * Helper method to build category path recursively
     */
    private void buildCategoryPath(Category category, List<Object[]> path) {
        if (category.getParent() != null) {
            buildCategoryPath(category.getParent(), path);
        }
        path.add(new Object[]{category.getId(), category.getName()});
    }

    /**
     * Get full category path as string
     */
    public String getCategoryPathString(Long categoryId) {
        List<Object[]> pathData = getCategoryPath(categoryId);

        if (pathData.isEmpty()) {
            return "";
        }

        StringBuilder path = new StringBuilder();
        for (int i = pathData.size() - 1; i >= 0; i--) {
            Object[] pathItem = pathData.get(i);
            String name = (String) pathItem[1];
            if (i < pathData.size() - 1) {
                path.append(" > ");
            }
            path.append(name);
        }

        return path.toString();
    }

    /**
     * Get category statistics
     */
    public CategoryStats getCategoryStats() {
        long totalCategories = categoryRepository.count();
        long activeCategories = categoryRepository.findByIsActiveTrueOrderBySortOrder(Pageable.unpaged()).getTotalElements();
        long featuredCategories = categoryRepository.findByIsFeaturedTrueAndIsActiveTrueOrderBySortOrder().size();
        long rootCategories = categoryRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrder().size();

        return CategoryStats.builder()
                .totalCategories(totalCategories)
                .activeCategories(activeCategories)
                .featuredCategories(featuredCategories)
                .rootCategories(rootCategories)
                .build();
    }

    /**
     * Get categories by level (depth in hierarchy)
     */
    public List<Category> getCategoriesByLevel(Long parentId) {
        return categoryRepository.findCategoriesByLevel(parentId);
    }

    /**
     * Check if category has subcategories
     */
    public boolean hasSubcategories(Long categoryId) {
        return categoryRepository.hasSubcategories(categoryId);
    }

    /**
     * Get recently added categories
     */
    public List<Category> getRecentlyAddedCategories(LocalDateTime since, Pageable pageable) {
        return categoryRepository.findRecentlyAddedCategories(since, pageable);
    }

    /**
     * Get next sort order for a parent category
     */
    private Integer getNextSortOrder(Category parent) {
        List<Category> siblings;
        if (parent != null) {
            siblings = categoryRepository.findByParentAndIsActiveTrueOrderBySortOrder(parent);
        } else {
            siblings = categoryRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrder();
        }

        if (siblings.isEmpty()) {
            return 1;
        }

        return siblings.get(siblings.size() - 1).getSortOrder() + 1;
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findAllParentCategories();
    }


    /**
     * DTO for category statistics
     */
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryStats {
        // Getters and setters
        private long totalCategories;
        private long activeCategories;
        private long featuredCategories;
        private long rootCategories;

        public static CategoryStatsBuilder builder() {
            return new CategoryStatsBuilder();
        }

        public static class CategoryStatsBuilder {
            private long totalCategories;
            private long activeCategories;
            private long featuredCategories;
            private long rootCategories;
            private final CategoryStats categoryStats = new CategoryStats();

            public CategoryStatsBuilder totalCategories(long totalCategories) {
                categoryStats.totalCategories = totalCategories;
                return this;
            }

            public CategoryStatsBuilder activeCategories(long activeCategories) {
                categoryStats.activeCategories = activeCategories;
                return this;
            }

            public CategoryStatsBuilder featuredCategories(long featuredCategories) {
                categoryStats.featuredCategories = featuredCategories;
                return this;
            }

            public CategoryStatsBuilder rootCategories(long rootCategories) {
                categoryStats.rootCategories = rootCategories;
                return this;
            }
            public CategoryStats build(){
                return new CategoryStats(totalCategories,activeCategories,featuredCategories,rootCategories);
            }
        }
    }
}

