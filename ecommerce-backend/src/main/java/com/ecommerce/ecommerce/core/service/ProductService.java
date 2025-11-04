package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.api.dto.product.*;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.exception.ErrorCode;
import com.ecommerce.ecommerce.core.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Product entity operations.
 * Handles product catalog management, search, and inventory operations.
 * Now follows separation of concerns by returning entities and using mappers for DTO conversion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService{

    private final ProductRepository productRepository;
    private final DtoMapper dtoMapper;

    /**
     * Get all active products with pagination
     */
    public Page<Product> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable);
    }

    /**
     * Get product by ID
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id)
                .filter(Product::getIsActive);
    }

    /**
     * Search products by term
     */
    public Page<Product> searchProductsByTerm(String searchTerm, Pageable pageable) {
        return productRepository.searchProducts(searchTerm, pageable);
    }

    /**
     * Search products with advanced filters
     */
    public Page<Product> searchProducts(ProductSearchRequest request, Pageable pageable) {
        return productRepository.findWithFilters(
                request.getCategoryId(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getInStock(),
                request.getSearchTerm(),
                pageable
        );
    }

    /**
     * Get featured products
     */
    public List<Product> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrueAndIsActiveTrue();
    }

    /**
     * Get products by category
     */
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Get related products
     */
    public List<Product> getRelatedProducts(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId)
                .filter(Product::getIsActive);

        if (productOpt.isEmpty()) {
            return List.of();
        }

        Product product = productOpt.get();
        return productRepository.findRelatedProducts(
                product.getCategory().getId(),
                productId,
                Pageable.ofSize(4)
        );
    }

    /**
     * Get products with low stock
     */
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    /**
     * Create new product
     */
    @Transactional
    public Product createProduct(ProductCreateRequest request) {
        Product product = dtoMapper.toProductEntity(request);
        product.setIsActive(true);
        product.setIsFeatured(false);

        Product savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getId());
        return savedProduct;
    }

    /**
     * Update product
     */
    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product", id));

        dtoMapper.updateProductFromRequest(request, product);
        Product savedProduct = productRepository.save(product);
        log.info("Product updated: {}", id);
        return savedProduct;
    }

    /**
     * Update product stock
     */
    @Transactional
    public Product updateStock(Long id, Integer stockQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product", id));

        product.setStockQuantity(stockQuantity);
        Product savedProduct = productRepository.save(product);
        log.info("Product stock updated: {} -> {}", id, stockQuantity);
        return savedProduct;
    }


    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product", id));

        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product deleted: {}", id);
    }


    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }
}
