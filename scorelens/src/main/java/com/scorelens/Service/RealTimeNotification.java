package com.scorelens.Service;


import com.google.firebase.messaging.FirebaseMessagingException;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Enums.WSFCMCode;
import com.scorelens.Enums.WebSocketTopic;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class RealTimeNotification {

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    FCMService fcmService;

    public void sendRealTimeNotification(
            Object message,
            WebSocketTopic topic,
            String tableID,
            WSFCMCode code
    ) {
        webSocketService.sendToWebSocket(
                topic.getValue() + tableID,
                new WebsocketReq(code, message)
        );
        try {
            fcmService.sendNotification(
                    tableID,
                    String.valueOf(code),
                    String.valueOf(message)
            );
        } catch (FirebaseMessagingException e) {
            log.warn("Cannot send message via FCM: {}", e.getMessage());
        }

    }

}
