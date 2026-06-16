package com.horseracing.controller;

import com.horseracing.dto.request.lane.LaneAssignRequest;
import com.horseracing.dto.response.lane.LaneResponse;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.service.LaneService;
import com.horseracing.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/lanes")
@Tag(name = "Lanes", description = "API quản lý làn chạy")
@SecurityRequirement(name = "bearerAuth")
public class LaneController {
    @Autowired
    private LaneService laneService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Phân công làn chạy")
    public ResponseEntity<ApiResponse<LaneResponse>> assignLane(
            @Valid @RequestBody LaneAssignRequest request,
            @RequestHeader("Authorization") String token) {
        Long assignedBy = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        LaneResponse response = laneService.assignLane(request, assignedBy);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Phân công làn thành công", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Cập nhật làn chạy")
    public ResponseEntity<ApiResponse<LaneResponse>> updateLane(
            @PathVariable Long id,
            @Valid @RequestBody LaneAssignRequest request,
            @RequestHeader("Authorization") String token) {
        Long updatedBy = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        LaneResponse response = laneService.updateLane(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật làn thành công", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết làn")
    public ResponseEntity<ApiResponse<LaneResponse>> getLane(@PathVariable Long id) {
        LaneResponse response = laneService.getLane(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/race/{raceId}")
    @Operation(summary = "Lấy danh sách làn của cuộc đua")
    public ResponseEntity<ApiResponse<List<LaneResponse>>> getLanesByRace(@PathVariable Long raceId) {
        List<LaneResponse> responses = laneService.getLanesByRace(raceId);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Xóa làn")
    public ResponseEntity<ApiResponse<Void>> deleteLane(@PathVariable Long id) {
        laneService.deleteLane(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa làn thành công", null));
    }
}
