package com.horseracing.websocket;

import com.horseracing.dto.response.notification.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationGateway {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendToUser(Long userId, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            notification
        );
    }

    public void broadcast(NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
