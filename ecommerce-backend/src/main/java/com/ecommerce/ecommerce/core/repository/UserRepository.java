package com.ecommerce.ecommerce.core.repository;

import com.ecommerce.ecommerce.core.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides methods for user authentication, profile management, and admin operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email (for authentication)
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email or username
     */
    Optional<User> findByEmailOrUsername(String email, String username);

    /**
     * Find user by provider and provider ID (for OAuth2)
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Find all active users with pagination
     */
    Page<User> findByIsActiveTrue(Pageable pageable);

    /**
     * Find users by role with pagination
     */
    Page<User> findByRole(User.Role role, Pageable pageable);

    /**
     * Find users created after a specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users by verification status
     */
    List<User> findByIsVerified(boolean isVerified);

    /**
     * Search users by name or email (case-insensitive)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find users who haven't logged in recently
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate OR u.lastLoginAt IS NULL")
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count total users
     */
    long count();

    /**
     * Count users by role
     */
    long countByRole(User.Role role);

    /**
     * Count verified users
     */
    long countByIsVerifiedTrue();

    /**
     * Find users for admin dashboard statistics
     */
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> getUserCountByRole();

    /**
     * Count active users
     */
    Long countByIsActiveTrue();

    /**
     * Count users created between two dates
     */
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count users by role
     */
    Long countByRole(String role);

    /**
     * Find recently registered users
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC")
    List<User> findRecentlyRegisteredUsers(@Param("since") LocalDateTime since, Pageable pageable);

  Long countByIsActiveIsTrue();
}
