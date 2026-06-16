package com.horseracing.repository;

import com.horseracing.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1")
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.action = ?1")
    Page<AuditLog> findByAction(String action, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1 AND a.action = ?2")
    Page<AuditLog> findByUserIdAndAction(Long userId, String action, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1 AND (?2 IS NULL OR a.createdAt >= ?2) AND (?3 IS NULL OR a.createdAt <= ?3)")
    Page<AuditLog> findByUserIdAndDateRange(Long userId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.action = ?1 AND (?2 IS NULL OR a.createdAt >= ?2) AND (?3 IS NULL OR a.createdAt <= ?3)")
    Page<AuditLog> findByActionAndDateRange(String action, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = ?1 AND a.action = ?2 AND (?3 IS NULL OR a.createdAt >= ?3) AND (?4 IS NULL OR a.createdAt <= ?4)")
    Page<AuditLog> findByUserIdAndActionAndDateRange(Long userId, String action, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
