package com.horseracing.service;

import com.horseracing.dto.response.notification.NotificationResponse;
import com.horseracing.dto.response.PageResponse;

public interface NotificationService {
    void sendApprovalNotification(Long userId, String title, String message, String targetType, Long targetId);
    void sendRejectionNotification(Long userId, String title, String message, String targetType, Long targetId);
    void broadcastResultNotification(String title, String message, String targetType, Long targetId);
    PageResponse<NotificationResponse> getNotifications(Long userId, int page, int size, Boolean isRead);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    long getUnreadCount(Long userId);
}
