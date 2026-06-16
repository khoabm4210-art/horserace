package com.horseracing.service;

import com.horseracing.dto.response.audit.AuditLogResponse;
import com.horseracing.dto.response.PageResponse;

import java.time.LocalDateTime;

public interface AuditLogService {
    void createAuditLog(Long userId, String action, String targetType, Long targetId, String description, String oldValue, String newValue, String ipAddress, String userAgent);
    PageResponse<AuditLogResponse> getAuditLogs(Long userId, String action, String fromDate, String toDate, int page, int size);
}
