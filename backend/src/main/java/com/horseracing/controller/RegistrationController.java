package com.horseracing.controller;

import com.horseracing.dto.request.registration.RegistrationCreateRequest;
import com.horseracing.dto.request.registration.RegistrationRejectRequest;
import com.horseracing.dto.response.registration.RegistrationResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.service.RegistrationService;
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
@RequestMapping("/api/v1/registrations")
@Tag(name = "Registrations", description = "API quản lý đăng ký tham gia cuộc đua")
@SecurityRequirement(name = "bearerAuth")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @PreAuthorize("hasRole('HORSE_OWNER')")
    @Operation(summary = "Tạo đăng ký mới")
    public ResponseEntity<ApiResponse<RegistrationResponse>> createRegistration(
            @Valid @RequestBody RegistrationCreateRequest request,
            @RequestHeader("Authorization") String token) {
        Long ownerId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        RegistrationResponse response = registrationService.createRegistration(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Đăng ký thành công", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết đăng ký")
    public ResponseEntity<ApiResponse<RegistrationResponse>> getRegistration(@PathVariable Long id) {
        RegistrationResponse response = registrationService.getRegistration(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Lấy tất cả đăng ký (ORGANIZER/ADMIN)")
    public ResponseEntity<ApiResponse<PageResponse<RegistrationResponse>>> getAllRegistrations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long raceId,
            @RequestParam(required = false) String status) {
        PageResponse<RegistrationResponse> response = registrationService.getAllRegistrations(page, size, raceId, status);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('HORSE_OWNER')")
    @Operation(summary = "Lấy đăng ký của tôi")
    public ResponseEntity<ApiResponse<PageResponse<RegistrationResponse>>> getMyRegistrations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token) {
        Long ownerId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        PageResponse<RegistrationResponse> response = registrationService.getMyRegistrations(ownerId, page, size, status);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Duyệt đăng ký")
    public ResponseEntity<ApiResponse<RegistrationResponse>> approveRegistration(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long approvedBy = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        RegistrationResponse response = registrationService.approveRegistration(id, approvedBy);
        return ResponseEntity.ok(ApiResponse.ok("Duyệt đăng ký thành công", response));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Từ chối đăng ký")
    public ResponseEntity<ApiResponse<RegistrationResponse>> rejectRegistration(
            @PathVariable Long id,
            @Valid @RequestBody RegistrationRejectRequest request,
            @RequestHeader("Authorization") String token) {
        Long rejectedBy = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        RegistrationResponse response = registrationService.rejectRegistration(id, request, rejectedBy);
        return ResponseEntity.ok(ApiResponse.ok("Từ chối đăng ký thành công", response));
    }
}
