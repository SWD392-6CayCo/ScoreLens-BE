package com.scorelens.Service.ConcreteHandler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.ProducerRequest;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WSFCMCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.FCMService;
import com.scorelens.Service.Interface.KafkaCodeHandler;
import com.scorelens.Service.Interface.customAnnotation.KafkaCodeMapping;
import com.scorelens.Service.KafkaService.HeartbeatService;
import com.scorelens.Service.RealTimeNotification;
import com.scorelens.Service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


@Component
@KafkaCodeMapping(KafkaCode.RUNNING)
@Slf4j
@RequiredArgsConstructor
public class RunningHandler implements KafkaCodeHandler {


    private final KafKaHeartBeat kafkaHeartBeat;

    private final HeartbeatService heartbeatService;

    private final RealTimeNotification realTimeNotification;

    @Override
    public void handle(ProducerRequest request) {
        String tableID = request.getTableID();
        kafkaHeartBeat.stop();
        kafkaHeartBeat.updateLastConfirmedTime();
        CompletableFuture<Boolean> tmp = heartbeatService.confirmHeartbeat();
        log.info("CompletableFuture: {}", tmp);
        realTimeNotification.sendRealTimeNotification(
                "AI Camera Connected",
                WebSocketTopic.NOTI_NOTIFICATION,
                tableID,
                WSFCMCode.NOTIFICATION
        );

    }
}
