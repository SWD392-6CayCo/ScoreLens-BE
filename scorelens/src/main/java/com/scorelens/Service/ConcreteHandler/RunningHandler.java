package com.scorelens.Service.ConcreteHandler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.ProducerRequest;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.FCMService;
import com.scorelens.Service.Interface.KafkaCodeHandler;
import com.scorelens.Service.Interface.customAnnotation.KafkaCodeMapping;
import com.scorelens.Service.KafkaService.HeartbeatService;
import com.scorelens.Service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;


@Component
@KafkaCodeMapping(KafkaCode.RUNNING)
@Slf4j
@RequiredArgsConstructor
public class RunningHandler implements KafkaCodeHandler {

    private KafKaHeartBeat kafkaHeartBeat;

    @Autowired
    private HeartbeatService heartbeatService;

    @Autowired
    private WebSocketService webSocketService;

    private final FCMService fcmService;

    @Override
    public void handle(ProducerRequest request) throws FirebaseMessagingException {
        String tableID = request.getTableID();
        kafkaHeartBeat.stop();
        kafkaHeartBeat.updateLastConfirmedTime();
        CompletableFuture<Boolean> tmp = heartbeatService.confirmHeartbeat();
        log.info("CompletableFuture: {}", tmp);
        webSocketService.sendToWebSocket(
                WebSocketTopic.NOTI_NOTIFICATION.getValue() + tableID,
                new WebsocketReq(WebSocketCode.NOTIFICATION, "AI Camera Connected")
        );
//        fcmService.sendNotification(
//                tableID,
//                "AI Camera Connected",
//                "noti"
//        );
    }
}
