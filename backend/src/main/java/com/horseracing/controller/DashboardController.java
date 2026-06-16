package com.horseracing.controller;

import com.horseracing.dto.response.dashboard.DashboardStatsResponse;
import com.horseracing.dto.response.race.RaceResponse;
import com.horseracing.dto.response.result.ResultResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.service.DashboardService;
import com.horseracing.security.JwtTokenProvider;
import com.horseracing.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "API dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Lấy thống kê theo role")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(auth -> auth.contains("ADMIN"));
        boolean isOrganizer = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(auth -> auth.contains("ORGANIZER"));

        DashboardStatsResponse response;
        if (isAdmin) {
            response = dashboardService.getStatsForAdmin();
        } else if (isOrganizer) {
            response = dashboardService.getStatsForOrganizer();
        } else {
            String username = authentication.getName();
            response = dashboardService.getStatsForHorseOwner(Long.valueOf(1));
        }
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Lấy cuộc đua sắp tới")
    public ResponseEntity<ApiResponse<PageResponse<RaceResponse>>> getUpcomingRaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        PageResponse<RaceResponse> response = dashboardService.getUpcomingRaces(page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/recent")
    @Operation(summary = "Lấy kết quả gần đây")
    public ResponseEntity<ApiResponse<PageResponse<ResultResponse>>> getRecentResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        PageResponse<ResultResponse> response = dashboardService.getRecentResults(page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
