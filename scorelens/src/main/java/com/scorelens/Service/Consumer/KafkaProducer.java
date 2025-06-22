package com.scorelens.Service.Consumer;

import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final int partition = 0;

    private final WebSocketService webSocketService;

    private final KafKaHeartBeat kafKaHeartBeat;

    private final String aiTopic = "ai-noti";

    public void sendEvent(String topic, Object object) {
        kafkaTemplate.send(topic, partition, null, object);
        kafkaTemplate.flush();
        log.info("Sending message: {}", object);
    }

    //cứ mỗi 10s, hàm sẽ run 1 lần
    @Scheduled(fixedDelay = 10000) //10s
    public void sendHeartbeat() {
        //gửi msg đến fastapi liên tục đến khi có cập nhật mới ở listener
        //k sợ miss flag vì KafkaHeartBeat đã khai báo @Component => singleton scope
        if (kafKaHeartBeat.isRunning()) {
            kafkaTemplate.send(aiTopic, partition, null, KafkaCode.RUNNING);
            log.info("Sent kafka code: {} to topic: {}", KafkaCode.RUNNING.toString(), aiTopic);
            webSocketService.sendToWebSocket(
                    "/topic/notification",
                    new WebsocketReq(WebSocketCode.NOTIFICATION, "Connecting to AI Camera...")
                    );
        } else {
            // Nếu quá 30s không nhận được confirm → bật lại
            if (kafKaHeartBeat.timeSinceLastConfirm().getSeconds() > 30) {
                log.warn("No confirmation received for 30s. Restarting heartbeat.");
                kafKaHeartBeat.start();
            }
        }

    }



}
