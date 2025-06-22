package com.scorelens.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
    SimpMessagingTemplate messagingTemplate;

    public void sendToWebSocket(String destination, Object message) {
        messagingTemplate.convertAndSend(destination, message);
    }

}
