package com.project.wallet_keeper.util.websocket;

import com.project.wallet_keeper.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String message) {
        messagingTemplate.convertAndSend("/topic/notification/", message);
    }

    public void sendNotification(String message, Long userId) {
        messagingTemplate.convertAndSend("/topic/notification/" + userId, message);
    }
}
