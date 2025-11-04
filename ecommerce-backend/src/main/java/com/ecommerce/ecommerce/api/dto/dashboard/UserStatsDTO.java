package com.ecommerce.ecommerce.api.dto.dashboard;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserStatsDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long newUsersToday;
    private Long newUsersThisWeek;
    private Long newUsersThisMonth;
    private Double userGrowthPercentage;
    private Long totalAdmins;
    private Long totalCustomers;
}
