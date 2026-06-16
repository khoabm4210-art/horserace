package com.horseracing.repository;

import com.horseracing.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.id = ?1")
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = ?1 AND n.isRead = ?2")
    Page<Notification> findByUserIdAndIsRead(Long userId, Integer isRead, Pageable pageable);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = ?1 AND n.isRead = ?2")
    long countByUserIdAndIsRead(Long userId, Integer isRead);
    
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = 1 WHERE n.user.id = ?1")
    void markAllAsRead(Long userId);
}
