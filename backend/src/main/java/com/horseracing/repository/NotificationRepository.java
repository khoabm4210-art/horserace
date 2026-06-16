package com.horseracing.repository;

import com.horseracing.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    Page<Notification> findByUserIdAndIsRead(Long userId, Integer isRead, Pageable pageable);
    long countByUserIdAndIsRead(Long userId, Integer isRead);
}
