package com.horseracing.controller;

import com.horseracing.dto.request.result.ResultEntryRequest;
import com.horseracing.dto.response.result.ResultResponse;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.service.ResultService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/results")
@Tag(name = "Results", description = "API quản lý kết quả cuộc đua")
@SecurityRequirement(name = "bearerAuth")
public class ResultController {
    @Autowired
    private ResultService resultService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Nhập kết quả cuộc đua")
    public ResponseEntity<ApiResponse<ResultResponse>> entryResult(
            @Valid @RequestBody ResultEntryRequest request,
            @RequestHeader("Authorization") String token) {
        Long enteredBy = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        ResultResponse response = resultService.entryResult(request, enteredBy);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Nhập kết quả thành công", response));
    }

    @PutMapping("/{raceId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Cập nhật kết quả")
    public ResponseEntity<ApiResponse<ResultResponse>> updateResult(
            @PathVariable Long raceId,
            @Valid @RequestBody ResultEntryRequest request,
            @RequestHeader("Authorization") String token) {
        Long updatedBy = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        ResultResponse response = resultService.updateResult(raceId, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật kết quả thành công", response));
    }

    @GetMapping("/{raceId}")
    @Operation(summary = "Lấy kết quả cuộc đua")
    public ResponseEntity<ApiResponse<ResultResponse>> getResult(@PathVariable Long raceId) {
        ResultResponse response = resultService.getResult(raceId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{raceId}/publish")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Công bố kết quả (tính điểm + cập nhật ranking)")
    public ResponseEntity<ApiResponse<ResultResponse>> publishResult(
            @PathVariable Long raceId,
            @RequestHeader("Authorization") String token) {
        Long publishedBy = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        ResultResponse response = resultService.publishResult(raceId, publishedBy);
        return ResponseEntity.ok(ApiResponse.ok("Công bố kết quả thành công", response));
    }
}
