package com.scorelens.Service.KafkaService;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Enums.WSFCMCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.FCMService;
import com.scorelens.Service.WebSocketService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class HeartbeatService {

    private final WebSocketService webSocketService;

    private final FCMService fcmService;

    //CompletableFuture => sau 10s k nhan duoc phan hoi, noti websocket
    private CompletableFuture<Boolean> heartbeatFuture;

    public CompletableFuture<Boolean> onHeartbeatChecking(String tableID) {
        heartbeatFuture = new CompletableFuture<>();
        heartbeatFuture.orTimeout(10, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.warn("No heartbeat confirmation received within 10s");
                    // Xử lý timeout => send noti to websocket & firebase
                    webSocketService.sendToWebSocket(
                            WebSocketTopic.NOTI_NOTIFICATION.getValue() + tableID,
                            new WebsocketReq(WSFCMCode.WARNING, "Camera not available")
                    );
                    try {
                        fcmService.sendNotification(
                                tableID,
                                String.valueOf(WSFCMCode.WARNING),
                                "Camera not available"
                        );
                    } catch (FirebaseMessagingException e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                });
        return heartbeatFuture;
    }

    public CompletableFuture<Boolean> confirmHeartbeat() {
        if (heartbeatFuture != null && !heartbeatFuture.isDone()) {
            heartbeatFuture.complete(true);
            log.info("Heartbeat confirmed");
        }
        return heartbeatFuture;
    }

}
