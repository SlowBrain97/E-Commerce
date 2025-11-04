package com.ecommerce.ecommerce.api.controller;

import com.ecommerce.ecommerce.api.dto.auth.*;
import com.ecommerce.ecommerce.api.dto.common.ApiResponse;
import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.core.exception.BusinessException;
import com.ecommerce.ecommerce.core.exception.ErrorCode;
import com.ecommerce.ecommerce.core.service.UserService;
import com.ecommerce.ecommerce.core.service.UserServiceProvider;
import com.ecommerce.ecommerce.integration.service.EmailService;
import com.ecommerce.ecommerce.util.JwtUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import com.ecommerce.ecommerce.util.UserPrincipal;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication controller handling user registration, login, and token management.
 * Provides endpoints for authentication, authorization, and user profile operations.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserServiceProvider userDetailsService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    /**
     * Register a new user account
     */
    @PostMapping("/register")
    @PermitAll
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Processing registration request for: {}", request.getEmail());

        User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
        );

        // Generate tokens for the new user
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .user(mapToUserInfo(user))
                .build();

        log.info("User registered successfully: {}", user.getId());
        emailService.sendWelcomeEmail(request.getEmail(),request.getUsername(),request.getFirstName());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User registered successfully", response));
    }

    /**
     * Authenticate user and return JWT tokens
     */
    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Processing login request for: {}", request.getEmailOrUsername());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailOrUsername(),
                        request.getPassword()
                )
        );

        // Generate tokens
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Get user info
        Optional<User> userOpt = userService.findByEmailOrUsername(userDetails.getUsername());
        User user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .user(mapToUserInfo(user))
                .build();

        log.info("User logged in successfully: {}", user.getId());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Login successful", response));
    }

    /**
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Validate refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token", null,null));
            }

            String username = jwtUtil.getUsernameFromToken(refreshToken);

            // Check if token is refresh token
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Token is not a refresh token", null,null));
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtUtil.getAccessTokenExpiration());

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Token refreshed successfully", response));

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token", null,null));
        }
    }

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserInfo>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User profile retrieved successfully", mapToUserInfo(user)));
    }

    /**
     * Change user password
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                          @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = jwtUtil.getUsernameFromToken(jwt);

        User userOpt = userService.findByEmail(username).orElseThrow(()-> new BusinessException(ErrorCode.AUTHENTICATION_FAILED,"Username not found"));
        userService.changePassword(request.getCurrentPassword(), request.getNewPassword(),userOpt);

        Map<String, String> response = Map.of("message", "Password changed successfully");
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Password changed successfully", response));
    }

    /**
     * Logout user (client-side token removal)
     */
    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout() {
        // In a stateless JWT implementation, logout is handled client-side
        // You might want to implement token blacklisting for better security
        Map<String, String> response = Map.of("message", "Logged out successfully");
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Logged out successfully", response));
    }

    /**
     * OAuth2 social login callback
     */
    //TODO: This endpoint is insecure and should be re-implemented.
    // The backend should handle the OAuth2 flow and get user information directly from the provider.
    @PostMapping("/oauth2/callback")
    @PermitAll
    public ResponseEntity<ApiResponse<AuthResponse>> oauth2Callback(@RequestBody Map<String, String> request) {
        try {
            String provider = request.get("provider");
            String providerId = request.get("providerId");
            String email = request.get("email");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");
            String avatarUrl = request.get("avatarUrl");

            User user = userService.createOAuth2User(provider, providerId, email, firstName, lastName, avatarUrl);

            // Generate tokens
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            AuthResponse response = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getAccessTokenExpiration())
                    .user(mapToUserInfo(user))
                    .build();

            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "OAuth2 authentication successful", response));

        } catch (Exception e) {
            log.error("OAuth2 callback failed", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "OAuth2 authentication failed: " + e.getMessage(), null,null));
        }
    }

    /**
     * Map User entity to UserInfo DTO
     */
    private UserInfo mapToUserInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .isVerified(user.getIsVerified())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
