package com.horseracing.dto.response.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private Long userId;
    private String username;
    private String action;
    private String targetType;
    private Long targetId;
    private String description;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String createdAt;
}
