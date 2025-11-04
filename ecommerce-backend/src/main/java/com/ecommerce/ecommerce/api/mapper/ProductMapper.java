package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.product.*;
import com.ecommerce.ecommerce.core.domain.entity.Product;
import com.ecommerce.ecommerce.core.domain.entity.ProductVariant;
import com.ecommerce.ecommerce.api.mapper.config.MapperConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper interface for Product entity and DTOs.
 * Uses MapStruct to automatically generate implementation.
 */
@Mapper(config = MapperConfiguration.class, uses = {CategoryMapper.class})
public interface ProductMapper {

    // Product to ProductResponse mapping
    @Mapping(target = "reviewCount", expression = "java(product.getReviews() != null ? product.getReviews().size() : 0)")
    @Mapping(target = "averageRating", expression = "java(product.getAverageRating())")
    @Mapping(target = "primaryImageUrl", expression = "java(product.getPrimaryImageUrl())")
    @Mapping(target = "allImageUrls", expression = "java(product.getAllImageUrls())")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "variants", source = "variants", qualifiedByName = "variantsToDto")
    ProductResponse productToProductResponse(Product product);

    // ProductResponse to Product entity mapping (reverse)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product productResponseToProduct(ProductResponse productResponse);

    // ProductVariant to ProductVariantDTO mapping
    @Mapping(target = "id", source = "id")
    @Named("variantsToDto")
    List<ProductVariantDTO> variantsToDto(List<ProductVariant> variants);

    // ProductVariantDTO to ProductVariant entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductVariant productVariantDtoToProductVariant(ProductVariantDTO variantDTO);

    // ProductVariant to ProductVariantDTO mapping (reverse)

    ProductVariantDTO productVariantToProductVariantDto(ProductVariant variant);

    // List mappings for batch operations
    @Mapping(target = "reviewCount", expression = "java(product.getReviews() != null ? product.getReviews().size() : 0)")
    @Mapping(target = "averageRating", expression = "java(product.getAverageRating())")
    @Mapping(target = "primaryImageUrl", expression = "java(product.getPrimaryImageUrl())")
    @Mapping(target = "allImageUrls", expression = "java(product.getAllImageUrls())")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "variants", source = "variants", qualifiedByName = "variantsToDto")
    List<ProductResponse> productsToProductResponses(List<Product> products);

    // ProductCreateRequest to Product entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product productCreateRequestToProduct(ProductCreateRequest request);

    // ProductUpdateRequest to Product entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProductFromRequest(ProductUpdateRequest request, @MappingTarget Product product);

    @Mapping(target = "id", source = "id")
    ProductVariant dtoToProductVariant(ProductVariantDTO variant);
}
