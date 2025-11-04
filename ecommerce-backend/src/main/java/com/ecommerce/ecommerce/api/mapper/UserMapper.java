package com.ecommerce.ecommerce.api.mapper;

import com.ecommerce.ecommerce.api.dto.user.UpdateProfileRequest;
import com.ecommerce.ecommerce.api.dto.user.UserResponse;
import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.api.mapper.config.MapperConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper interface for User entity and DTOs.
 * Uses MapStruct to automatically generate implementation.
 */
@Mapper(config = MapperConfiguration.class)
public interface UserMapper {

    // User to UserResponse mapping
    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    UserResponse userToUserResponse(User user);

    // UserResponse to User entity mapping (reverse)
    @Mapping(target = "role", ignore = true) // Role should be handled separately
    @Mapping(target = "password", ignore = true) // Password should be handled separately
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    User userResponseToUser(UserResponse userResponse);

    // List mapping for batch operations
    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    List<UserResponse> usersToUserResponses(List<User> users);

    // UpdateProfileRequest to User mapping (for updating existing user)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true) // Email updates should be handled separately for security
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    void updateUserFromProfileRequest(UpdateProfileRequest request, @MappingTarget User user);

}

