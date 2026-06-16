package com.horseracing.service.impl;

import com.horseracing.dto.response.notification.NotificationResponse;
import com.horseracing.dto.response.PageResponse;
import com.horseracing.entity.Notification;
import com.horseracing.entity.User;
import com.horseracing.enums.NotificationType;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.NotificationRepository;
import com.horseracing.repository.UserRepository;
import com.horseracing.service.NotificationService;
import com.horseracing.websocket.NotificationGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationGateway notificationGateway;

    @Override
    public void sendApprovalNotification(Long userId, String title, String message, String targetType, Long targetId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Notification notification = Notification.builder()
            .user(user)
            .title(title)
            .message(message)
            .type(NotificationType.APPROVAL)
            .isRead(0)
            .targetType(targetType)
            .targetId(targetId)
            .createdAt(LocalDateTime.now())
            .build();
        
        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(saved);
        notificationGateway.sendToUser(userId, response);
        log.info("Approval notification sent to user: {}", userId);
    }

    @Override
    public void sendRejectionNotification(Long userId, String title, String message, String targetType, Long targetId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Notification notification = Notification.builder()
            .user(user)
            .title(title)
            .message(message)
            .type(NotificationType.REJECTION)
            .isRead(0)
            .targetType(targetType)
            .targetId(targetId)
            .createdAt(LocalDateTime.now())
            .build();
        
        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(saved);
        notificationGateway.sendToUser(userId, response);
        log.info("Rejection notification sent to user: {}", userId);
    }

    @Override
    public void broadcastResultNotification(String title, String message, String targetType, Long targetId) {
        NotificationResponse response = NotificationResponse.builder()
            .title(title)
            .message(message)
            .type("RESULT")
            .targetType(targetType)
            .targetId(targetId)
            .createdAt(LocalDateTime.now().toString())
            .build();
        
        notificationGateway.broadcast(response);
        log.info("Result notification broadcasted");
    }

    @Override
    public PageResponse<NotificationResponse> getNotifications(Long userId, int page, int size, Boolean isRead) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications;
        
        if (isRead != null) {
            notifications = notificationRepository.findByUserIdAndIsRead(userId, isRead ? 1 : 0, pageable);
        } else {
            notifications = notificationRepository.findByUserId(userId, pageable);
        }
        
        return PageResponse.<NotificationResponse>builder()
            .content(notifications.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
            .page(page)
            .size(size)
            .totalElements(notifications.getTotalElements())
            .totalPages(notifications.getTotalPages())
            .last(notifications.isLast())
            .build();
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setIsRead(1);
        notificationRepository.save(notification);
        log.info("Notification marked as read: {}", notificationId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
        log.info("All notifications marked as read for user: {}", userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, 0);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .type(notification.getType().name())
            .isRead(notification.getIsRead() == 1)
            .targetType(notification.getTargetType())
            .targetId(notification.getTargetId())
            .createdAt(notification.getCreatedAt().toString())
            .build();
    }
}
