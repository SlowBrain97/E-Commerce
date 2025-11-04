package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.auth.UserInfo;
import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.api.mapper.config.MapperConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for authentication DTOs.
 * Uses MapStruct to automatically generate implementation.
 */
@Mapper(config = MapperConfiguration.class)
public interface AuthMapper {

    // User to UserInfo mapping (for auth response)
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    UserInfo userToUserInfo(User user);

}
