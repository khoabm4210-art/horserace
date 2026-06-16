package com.horseracing.repository;

import com.horseracing.entity.AuditLog;
import com.horseracing.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
    Page<AuditLog> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
