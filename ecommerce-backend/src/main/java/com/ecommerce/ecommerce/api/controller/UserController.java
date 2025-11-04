package com.ecommerce.ecommerce.api.controller;

import com.ecommerce.ecommerce.api.dto.common.ApiResponse;
import com.ecommerce.ecommerce.api.dto.user.UpdateProfileRequest;
import com.ecommerce.ecommerce.api.dto.user.UserResponse;
import com.ecommerce.ecommerce.api.mapper.DtoMapper;
import com.ecommerce.ecommerce.core.domain.entity.User;
import com.ecommerce.ecommerce.core.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final DtoMapper dtoMapper;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile(Authentication authentication) {
        User user = userService.getCurrentUserProfile(authentication);
        UserResponse response = dtoMapper.toUserResponseDTO(user);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile retrieved successfully", response));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@PathVariable Long userId, Authentication authentication) {
        User user = userService.getUserProfile(userId, authentication);
        UserResponse response = dtoMapper.toUserResponseDTO(user);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User profile retrieved successfully", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        User user = userService.updateProfile(request, authentication);
        UserResponse response = dtoMapper.toUserResponseDTO(user);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile updated successfully", response));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @RequestParam String token,
            Authentication authentication) {
        userService.verifyEmail(token, authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Email verified successfully", "Email verified successfully"));
    }

    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateAccount(Authentication authentication) {
        userService.deactivateAccount(authentication);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Account deactivated successfully", "Account deactivated successfully"));
    }

    // Admin only endpoints

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserResponse> response = users.map(dtoMapper::toUserResponseDTO);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Users retrieved successfully", response));
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.searchUsers(query, pageable);
        Page<UserResponse> response = users.map(dtoMapper::toUserResponseDTO);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User search completed successfully", response));
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        Map<String, Object> response = userService.getUserStats();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User statistics retrieved successfully", response));
    }
}
