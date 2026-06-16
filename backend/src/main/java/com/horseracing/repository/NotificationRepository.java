package com.horseracing.repository;

import com.horseracing.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 ORDER BY n.createdAt DESC")
    Page<Notification> findByUser(Long userId, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = ?1 AND n.isRead = 0")
    long countUnreadByUser(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 AND n.isRead = 0")
    Page<Notification> findUnreadByUser(Long userId, Pageable pageable);
}
