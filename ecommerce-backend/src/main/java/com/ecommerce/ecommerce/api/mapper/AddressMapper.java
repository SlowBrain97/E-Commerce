package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.order.AddressDTO;
import com.ecommerce.ecommerce.core.domain.entity.Address;
import com.ecommerce.ecommerce.api.mapper.config.MapperConfiguration;
import org.mapstruct.Mapper;

/**
 * Mapper interface for Address entity and DTOs.
 * Uses MapStruct to automatically generate implementation.
 */
@Mapper(config = MapperConfiguration.class)
public interface AddressMapper {

    // Address entity to AddressDTO mapping
    AddressDTO addressToAddressDTO(Address address);

    // AddressDTO to Address entity mapping
    Address addressDTOToAddress(AddressDTO addressDTO);

}
