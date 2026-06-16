package com.horseracing.service;

import com.horseracing.dto.response.notification.NotificationResponse;
import com.horseracing.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NotificationResponse> getNotifications(Long userId, Pageable pageable, Boolean isRead);
    
    NotificationResponse markAsRead(Long notificationId);
    
    void markAllAsRead(Long userId);
    
    long getUnreadCount(Long userId);
    
    void createNotification(Long userId, String title, String message, NotificationType type, 
                           String targetType, Long targetId);
    
    void createBroadcastNotification(String title, String message, NotificationType type);
}
