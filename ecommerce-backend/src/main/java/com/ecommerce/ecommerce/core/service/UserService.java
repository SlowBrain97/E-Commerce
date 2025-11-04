package com.ecommerce.ecommerce.core.service;

import com.ecommerce.ecommerce.api.dto.user.UpdateProfileRequest;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.core.exception.BusinessException;
import com.ecommerce.ecommerce.core.exception.ErrorCode;
import com.ecommerce.ecommerce.core.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Service class for User entity operations.
 * Handles user registration, profile management, and admin operations.
 * Now follows separation of concerns by returning entities and using mappers for DTO conversion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final DtoMapper dtoMapper;

    /**
     * Register a new user
     */
    @Transactional
    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        log.info("Registering new user: {}", email);

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTRY, "User with email " + email + " already exists");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENTRY, "Username " + username + " already exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .role(User.Role.USER)
                .isActive(true)
                .isVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Get current user profile as entity
     */
    public User getCurrentUserProfile(Authentication authentication) {
        String userId = authenticationService.getCurrentUserId(authentication);
        return findById(Long.parseLong(userId))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "User", userId));
    }

    /**
     * Get current user entity
     */
    public User getCurrentUser(Authentication authentication) {
        return authenticationService.getCurrentUser(authentication);
    }
    /**
     * Get user profile by ID (for admin or self)
     */
    public User getUserProfile(Long userId, Authentication authentication) {
        authenticationService.validateUserAccess(userId, authentication);
        return findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "User", userId));
    }

    /**
     * Update user profile
     */
    @Transactional
    public User updateProfile(UpdateProfileRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);

        // Update profile fields using DtoMapper
        dtoMapper.updateUserFromProfileRequest(request, user);

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException(ErrorCode.DUPLICATE_ENTRY, "Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        User savedUser = userRepository.save(user);
        log.info("Profile updated for user: {}", user.getId());
        return savedUser;
    }

    /**
     * Change password
     */
    @Transactional
    public void changePassword(String currentPassword, String newPassword, User user) {

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getId());
    }

    /**
     * Verify email
     */
    @Transactional
    public void verifyEmail(String token, Authentication authentication) {
        // In a real application, you'd validate the token
        // For now, we'll just verify the current user
        User user = getCurrentUser(authentication);
        user.setIsVerified(true);
        userRepository.save(user);

        log.info("Email verified for user: {}", user.getId());
    }

    /**
     * Deactivate account
     */
    @Transactional
    public void deactivateAccount(Authentication authentication) {
        User user = getCurrentUser(authentication);
        user.setIsActive(false);
        userRepository.save(user);

        log.info("Account deactivated: {}", user.getId());
    }

    /**
     * Get all users (admin only)
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable);
    }

    /**
     * Search users (admin only)
     */
    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepository.searchUsers(query, pageable);
    }

    /**
     * Get user statistics
     */
    public Map<String, Object> getUserStats() {
        long totalUsers = userRepository.count();
        long verifiedUsers = userRepository.countByIsVerifiedTrue();
        long adminUsers = userRepository.countByRole(User.Role.ADMIN);

        return Map.of(
            "totalUsers", totalUsers,
            "verifiedUsers", verifiedUsers,
            "adminUsers", adminUsers,
            "unverifiedUsers", totalUsers - verifiedUsers
        );
    }

    // Helper methods

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }



  public User createOAuth2User(String provider, String providerId, String email, String firstName, String lastName, String avatarUrl) {
      log.info("Registering new user: {}", email);

      // Check if user already exists
      if (userRepository.existsByEmail(email)) {
          throw new BusinessException(ErrorCode.DUPLICATE_ENTRY, "User with email " + email + " already exists");
      }
      User user = User.builder()
              .email(email)
              .provider(provider)
              .avatarUrl(avatarUrl)
              .providerId(providerId)
              .firstName(firstName)
              .lastName(lastName)
              .role(User.Role.USER)
              .isActive(true)
              .isVerified(false)
              .build();

      User savedUser = userRepository.save(user);
      log.info("User registered successfully: {}", savedUser.getId());

      return savedUser;
  }

    public Optional<User> findByEmailOrUsername(String username) {
        return userRepository.findByEmailOrUsername(username,username);
    }
}
