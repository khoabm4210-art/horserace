package com.horseracing.service.impl;

import com.horseracing.dto.response.audit.AuditLogResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.entity.AuditLog;
import com.horseracing.entity.User;
import com.horseracing.repository.AuditLogRepository;
import com.horseracing.repository.UserRepository;
import com.horseracing.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void createAuditLog(Long userId, String action, String targetType, Long targetId, String description, String oldValue, String newValue, String ipAddress, String userAgent) {
        Optional<User> user = userRepository.findById(userId);
        
        AuditLog auditLog = AuditLog.builder()
            .user(user.orElse(null))
            .username(user.map(User::getUsername).orElse("SYSTEM"))
            .action(action)
            .targetType(targetType)
            .targetId(targetId)
            .description(description)
            .oldValue(oldValue)
            .newValue(newValue)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .createdAt(LocalDateTime.now())
            .build();
        
        auditLogRepository.save(auditLog);
        log.info("Audit log created: {} - {}", action, targetType);
    }

    @Override
    public PageResponse<AuditLogResponse> getAuditLogs(Long userId, String action, String fromDate, String toDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        
        if (fromDate != null) {
            startDate = LocalDateTime.parse(fromDate, formatter);
        }
        if (toDate != null) {
            endDate = LocalDateTime.parse(toDate, formatter);
        }

        Page<AuditLog> logs;
        if (userId != null && action != null) {
            logs = auditLogRepository.findByUserIdAndActionAndDateRange(userId, action, startDate, endDate, pageable);
        } else if (userId != null) {
            logs = auditLogRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable);
        } else if (action != null) {
            logs = auditLogRepository.findByActionAndDateRange(action, startDate, endDate, pageable);
        } else {
            logs = auditLogRepository.findAll(pageable);
        }

        return PageResponse.<AuditLogResponse>builder()
            .content(logs.getContent().stream()
                .map(a -> AuditLogResponse.builder()
                    .id(a.getId())
                    .userId(a.getUser() != null ? a.getUser().getId() : null)
                    .username(a.getUsername())
                    .action(a.getAction())
                    .targetType(a.getTargetType())
                    .targetId(a.getTargetId())
                    .description(a.getDescription())
                    .oldValue(a.getOldValue())
                    .newValue(a.getNewValue())
                    .ipAddress(a.getIpAddress())
                    .createdAt(a.getCreatedAt().toString())
                    .build())
                .collect(Collectors.toList()))
            .page(page)
            .size(size)
            .totalElements(logs.getTotalElements())
            .totalPages(logs.getTotalPages())
            .last(logs.isLast())
            .build();
    }
}
