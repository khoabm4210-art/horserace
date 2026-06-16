package com.horseracing.repository;

import com.horseracing.entity.AuditLog;
import com.horseracing.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT al FROM AuditLog al WHERE al.userId = ?1 ORDER BY al.createdAt DESC")
    Page<AuditLog> findByUser(Long userId, Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.action = ?1 ORDER BY al.createdAt DESC")
    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN ?1 AND ?2 ORDER BY al.createdAt DESC")
    Page<AuditLog> findByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
