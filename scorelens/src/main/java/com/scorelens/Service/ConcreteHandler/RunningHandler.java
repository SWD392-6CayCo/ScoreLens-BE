package com.scorelens.Service.ConcreteHandler;

import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.ProducerRequest;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.Interface.KafkaCodeHandler;
import com.scorelens.Service.Interface.customAnnotation.KafkaCodeMapping;
import com.scorelens.Service.KafkaService.HeartbeatService;
import com.scorelens.Service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;


@Component
@KafkaCodeMapping(KafkaCode.RUNNING)
@Slf4j
public class RunningHandler implements KafkaCodeHandler {

    @Autowired
    private KafKaHeartBeat kafkaHeartBeat;

    @Autowired
    private HeartbeatService heartbeatService;

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void handle(ProducerRequest request) {
        String tableID = request.getTableID();
        kafkaHeartBeat.stop();
        kafkaHeartBeat.updateLastConfirmedTime();
        CompletableFuture<Boolean> tmp = heartbeatService.confirmHeartbeat();
        log.info("CompletableFuture: {}", tmp);
        webSocketService.sendToWebSocket(
                WebSocketTopic.NOTI_NOTIFICATION.getValue() + tableID,
                new WebsocketReq(WebSocketCode.NOTIFICATION, "AI Camera Connected")
        );
    }
}
