package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.core.exception.BusinessException;
import com.ecommerce.ecommerce.core.exception.ErrorCode;
import com.ecommerce.ecommerce.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication-related operations.
 * Extracted from UserService to follow separation of concerns.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;

    /**
     * Get current authenticated user ID
     */
    public String getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            User user = userRepository.findByEmailOrUsername(userDetails.getUsername(), userDetails.getUsername())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "User not found"));
            return user.getId().toString();
        }

        throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "Invalid authentication principal");
    }

    /**
     * Get current authenticated user entity
     */
    public User getCurrentUser(Authentication authentication) {
        String userId = getCurrentUserId(authentication);
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "User not found"));
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Validate if user can access another user's data
     */
    public void validateUserAccess(Long targetUserId, Authentication authentication) {
        String currentUserId = getCurrentUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        boolean isOwnProfile = currentUserId.equals(targetUserId.toString());

        if (!isOwnProfile && !isAdmin) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Access denied");
        }
    }
}
