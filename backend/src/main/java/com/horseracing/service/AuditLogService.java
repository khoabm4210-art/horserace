package com.horseracing.service;

import com.horseracing.dto.response.audit.AuditLogResponse;
import com.horseracing.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AuditLogService {
    Page<AuditLogResponse> getAuditLogs(Pageable pageable, Long userId, String action, 
                                        String targetType, LocalDate fromDate, LocalDate toDate);
    
    void logAction(Long userId, String username, AuditAction action, String targetType, 
                   Long targetId, String description, String oldValue, String newValue, 
                   String ipAddress, String userAgent);
}
