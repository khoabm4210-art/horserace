package com.horseracing.controller;

import com.horseracing.dto.request.horse.HorseCreateRequest;
import com.horseracing.dto.request.horse.HorseUpdateRequest;
import com.horseracing.dto.request.horse.HorseRejectRequest;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.dto.response.horse.HorseResponse;
import com.horseracing.security.JwtTokenProvider;
import com.horseracing.service.HorseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/horses")
@Tag(name = "Horses", description = "Horse management endpoints")
public class HorseController {
    @Autowired
    private HorseService horseService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping
    @Operation(summary = "Get all horses")
    public ResponseEntity<ApiResponse<PageResponse<HorseResponse>>> getAllHorses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ownerId) {
        PageResponse<HorseResponse> response = horseService.getAllHorses(page, size, sort, keyword, status, ownerId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get horse by ID")
    public ResponseEntity<ApiResponse<HorseResponse>> getHorse(@PathVariable Long id) {
        HorseResponse response = horseService.getHorse(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @Operation(summary = "Create new horse")
    public ResponseEntity<ApiResponse<HorseResponse>> createHorse(
            @Valid @RequestBody HorseCreateRequest request,
            Authentication authentication) {
        String token = authentication.getCredentials().toString();
        Long ownerId = tokenProvider.getUserIdFromToken(token);
        HorseResponse response = horseService.createHorse(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Tạo ngựa thành công", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update horse")
    public ResponseEntity<ApiResponse<HorseResponse>> updateHorse(
            @PathVariable Long id,
            @Valid @RequestBody HorseUpdateRequest request,
            Authentication authentication) {
        String token = authentication.getCredentials().toString();
        Long ownerId = tokenProvider.getUserIdFromToken(token);
        HorseResponse response = horseService.updateHorse(id, request, ownerId);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật ngựa thành công", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete horse")
    public ResponseEntity<ApiResponse<Void>> deleteHorse(
            @PathVariable Long id,
            Authentication authentication) {
        String token = authentication.getCredentials().toString();
        Long ownerId = tokenProvider.getUserIdFromToken(token);
        horseService.deleteHorse(id, ownerId);
        return ResponseEntity.ok(ApiResponse.ok("Xóa ngựa thành công", null));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve horse")
    public ResponseEntity<ApiResponse<HorseResponse>> approveHorse(@PathVariable Long id) {
        HorseResponse response = horseService.approveHorse(id);
        return ResponseEntity.ok(ApiResponse.ok("Duyệt ngựa thành công", response));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject horse")
    public ResponseEntity<ApiResponse<HorseResponse>> rejectHorse(
            @PathVariable Long id,
            @Valid @RequestBody HorseRejectRequest request) {
        HorseResponse response = horseService.rejectHorse(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Từ chối ngựa thành công", response));
    }

    @PatchMapping("/{id}/disqualify")
    @Operation(summary = "Disqualify horse")
    public ResponseEntity<ApiResponse<HorseResponse>> disqualifyHorse(
            @PathVariable Long id,
            @Valid @RequestBody HorseRejectRequest request) {
        HorseResponse response = horseService.disqualifyHorse(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Loại ngựa thành công", response));
    }
}
