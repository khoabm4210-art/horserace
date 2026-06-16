package com.horseracing.controller;

import com.horseracing.dto.response.audit.AuditLogResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "API nhật ký kiểm toán")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Lấy danh sách audit logs")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<AuditLogResponse> response = auditLogService.getAuditLogs(userId, action, fromDate, toDate, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
