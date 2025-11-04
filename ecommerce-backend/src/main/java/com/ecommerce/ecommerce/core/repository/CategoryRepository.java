package com.ecommerce.ecommerce.core.repository;

import com.ecommerce.ecommerce.core.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity operations.
 * Provides methods for category management and hierarchical queries.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find root categories (no parent)
     */
    List<Category> findByParentIsNullAndIsActiveTrueOrderBySortOrder();

    /**
     * Find active categories with pagination
     */
    Page<Category> findByIsActiveTrueOrderBySortOrder(Pageable pageable);

    /**
     * Find categories by parent
     */
    List<Category> findByParentAndIsActiveTrueOrderBySortOrder(Category parent);

    /**
     * Find category by name
     */
    Optional<Category> findByNameAndIsActiveTrue(String name);

    /**
     * Find featured categories
     */
    List<Category> findByIsFeaturedTrueAndIsActiveTrueOrderBySortOrder();

    /**
     * Find subcategories by parent ID
     */
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.isActive = true ORDER BY c.sortOrder")
    List<Category> findSubcategoriesByParentId(@Param("parentId") Long parentId);

    /**
     * Find all parent categories (for dropdown selection)
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.sortOrder")
    List<Category> findAllParentCategories();

    /**
     * Search categories by name
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY c.sortOrder")
    List<Category> searchCategories(@Param("searchTerm") String searchTerm);

    /**
     * Find categories with products
     */
    @Query("SELECT DISTINCT c FROM Category c " +
           "JOIN c.products p " +
           "WHERE c.isActive = true AND p.isActive = true " +
           "ORDER BY c.sortOrder")
    List<Category> findCategoriesWithProducts();

    /**
     * Get category hierarchy as a flat list
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY " +
           "CASE WHEN c.parent IS NULL THEN c.id ELSE c.parent.id END, " +
           "c.parent.id NULLS FIRST, c.sortOrder")
    List<Category> findAllInHierarchyOrder();

    /**
     * Find categories by level (depth in hierarchy)
     */
    @Query("SELECT c FROM Category c WHERE " +
           "(:parentId IS NULL AND c.parent IS NULL) OR " +
           "(:parentId IS NOT NULL AND c.parent.id = :parentId) " +
           "AND c.isActive = true " +
           "ORDER BY c.sortOrder")
    List<Category> findCategoriesByLevel(@Param("parentId") Long parentId);

    /**
     * Count products in category (including subcategories)
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE " +
           "(p.category.id = :categoryId OR p.category.parent.id = :categoryId) AND " +
           "p.isActive = true")
    long countProductsInCategoryIncludingSubcategories(@Param("categoryId") Long categoryId);

    /**
     * Count direct products in category
     */
    long countByIdAndIsActiveTrue(Long categoryId);

    /**
     * Find categories sorted by product count
     */
    @Query("SELECT c, COUNT(p) as productCount FROM Category c " +
           "LEFT JOIN c.products p " +
           "WHERE c.isActive = true AND p.isActive = true " +
           "GROUP BY c " +
           "ORDER BY productCount DESC, c.sortOrder")
    List<Category> findCategoriesByProductCount();

    /**
     * Get category path (breadcrumb) - simplified version
     */
    @Query("SELECT c FROM Category c WHERE c.id = :categoryId AND c.isActive = true")
    Optional<Category> findCategoryForPath(@Param("categoryId") Long categoryId);

    /**
     * Find top level categories for navigation
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.sortOrder")
    List<Category> findTopLevelCategories();

    /**
     * Check if category has subcategories
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.parent.id = :categoryId AND c.isActive = true")
    boolean hasSubcategories(@Param("categoryId") Long categoryId);

    /**
     * Find categories for admin management
     */
    @Query("SELECT c FROM Category c WHERE c.createdAt >= :since ORDER BY c.createdAt DESC")
    List<Category> findRecentlyAddedCategories(@Param("since") java.time.LocalDateTime since, Pageable pageable);

    /**
     * Get category statistics
     */
    @Query("SELECT c.parent.id, COUNT(c) FROM Category c WHERE c.isActive = true GROUP BY c.parent.id")
    List<Object[]> getCategoryStats();
}
