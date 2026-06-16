package com.horseracing.service;

import com.horseracing.dto.response.dashboard.DashboardStatsResponse;
import com.horseracing.dto.response.race.RaceResponse;

import java.util.List;

public interface DashboardService {
    DashboardStatsResponse getStats(Long userId);
    
    List<RaceResponse> getUpcomingRaces(int limit);
    
    List<RaceResponse> getRecentResults(int limit);
}
