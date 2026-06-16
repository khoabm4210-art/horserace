package com.horseracing.service;

import com.horseracing.dto.response.dashboard.DashboardStatsResponse;
import com.horseracing.dto.response.race.RaceResponse;
import com.horseracing.dto.response.result.ResultResponse;
import com.horseracing.dto.response.PageResponse;

public interface DashboardService {
    DashboardStatsResponse getStatsForAdmin();
    DashboardStatsResponse getStatsForOrganizer();
    DashboardStatsResponse getStatsForHorseOwner(Long ownerId);
    PageResponse<RaceResponse> getUpcomingRaces(int page, int size);
    PageResponse<ResultResponse> getRecentResults(int page, int size);
}
