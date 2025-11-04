package com.ecommerce.ecommerce.api.mapper.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Configuration class for MapStruct mappers.
 * Defines common settings for all mappers in the application.
 */
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
public interface MapperConfiguration {
}
