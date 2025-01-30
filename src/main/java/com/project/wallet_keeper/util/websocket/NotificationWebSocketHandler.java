package com.project.wallet_keeper.util.websocket;

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
        log.info("예산 초과!!");
        messagingTemplate.convertAndSend("/topic/notification", message);
    }
}
