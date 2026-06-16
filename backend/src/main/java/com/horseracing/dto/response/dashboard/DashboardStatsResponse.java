package com.horseracing.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    // For Admin
    private Long totalUsers;
    private Long totalHorses;
    private Long totalRaces;
    private Long completedRaces;
    private Long pendingRaces;
    
    // For Organizer
    private Long openRaces;
    private Long ongoingRaces;
    private Long pendingRegistrations;
    private Long approvedRegistrations;
    
    // For Horse Owner
    private Long myHorses;
}
