package com.ecommerce.ecommerce.api.dto.auth;

import com.ecommerce.ecommerce.core.service.UserService;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserStats {
  private long totalUsers;
  private long verifiedUsers;
  private long adminUsers;
  private long unverifiedUsers;
}