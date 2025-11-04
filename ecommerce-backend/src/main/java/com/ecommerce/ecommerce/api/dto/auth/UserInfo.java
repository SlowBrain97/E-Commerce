package com.ecommerce.ecommerce.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user information in auth response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

  private Long id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String fullName;
  private String role;
  private Boolean isVerified;
  private String avatarUrl;
}

