package com.ecommerce.ecommerce.api.controller;

import com.ecommerce.ecommerce.api.dto.common.ApiResponse;
import com.ecommerce.ecommerce.api.dto.common.PageResponse;
import com.ecommerce.ecommerce.api.dto.product.*;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Product controller handling product catalog operations.
 * Provides endpoints for browsing, searching, and managing products.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class ProductController {

    private final ProductService productService;
    private final DtoMapper mapper;
    /**
     * Get all active products with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Product> products = productService.getAllActiveProducts(pageable);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Products retrieved successfully", mapper.toPageDto(products,mapper::toProductResponseDTO)));
    }

    /**
     * Search products with filters
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(@Valid @RequestBody ProductSearchRequest request) {

        Sort.Direction direction = request.getSortDirection().equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, request.getSortBy())
        );

        Page<Product> response = productService.searchProducts(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Products searched successfully", mapper.toPageDto(response,mapper::toProductResponseDTO)));
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        Product response = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Product retrieved successfully", mapper.toProductResponseDTO(response)));
    }

    /**
     * Get featured products
     */
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeaturedProducts() {
        List<Product> response = productService.getFeaturedProducts();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Featured products retrieved successfully", mapper.toProductResponseDTOs(response)));
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> response = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Products by category retrieved successfully", mapper.toPageDto(response,mapper::toProductResponseDTO)));
    }

    /**
     * Search products by name or description
     */
    @GetMapping("/search/simple")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> simpleSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> response = productService.searchProductsByTerm(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Simple search completed successfully", mapper.toPageDto(response,mapper::toProductResponseDTO)));
    }

    /**
     * Create new product (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Product response = productService.createProduct(request);
        log.info("Product created: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Product created successfully", mapper.toProductResponseDTO(response)));
    }

    /**
     * Update product (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {

        Product response = productService.updateProduct(id, request);
        log.info("Product updated: {}", id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Product updated successfully", mapper.toProductResponseDTO(response)));
    }

    /**
     * Delete product (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        log.info("Product deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Product deleted successfully", "Product deleted successfully"));
    }

    /**
     * Update product stock (Admin only)
     */
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
            @PathVariable Long id,
            @RequestParam Integer stockQuantity) {

        Product response = productService.updateStock(id, stockQuantity);
        log.info("Product stock updated: {} -> {}", id, stockQuantity);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Product stock updated successfully", mapper.toProductResponseDTO(response)));
    }

    /**
     * Get related products
     */
    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getRelatedProducts(@PathVariable Long id) {
        List<Product> response = productService.getRelatedProducts(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Related products retrieved successfully", mapper.toProductResponseDTOs(response)));
    }

    /**
     * Get products with low stock (Admin only)
     */
    @GetMapping("/admin/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {

        List<Product> response = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Low stock products retrieved successfully", mapper.toProductResponseDTOs(response)));
    }
}
