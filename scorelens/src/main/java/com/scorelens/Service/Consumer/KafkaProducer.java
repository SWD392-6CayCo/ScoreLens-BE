package com.scorelens.Service.Consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.ProducerRequest;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ObjectMapper objectMapper;

    public void sendEvent(String topic, Object object) {
        kafkaTemplate.send(topic, partition, null, object);
        kafkaTemplate.flush();
        log.info("Sending message: {}", object);
    }

    //cứ mỗi 10s, hàm sẽ run 1 lần
    //gửi msg đến fastapi liên tục đến khi có cập nhật mới ở listener
    //k sợ miss flag vì KafkaHeartBeat đã khai báo @Component => singleton scope
//    @Scheduled(fixedDelay = 10000)
    public void sendHeartbeat() {
        if (kafKaHeartBeat.isRunning()) {
            try {
                String message = objectMapper.writeValueAsString(new ProducerRequest(KafkaCode.RUNNING, "Heart beat checking"));
                kafkaTemplate.send(aiTopic, partition, null, message);
                log.info("Sent kafka message: {} to topic: {}", message, aiTopic);

                webSocketService.sendToWebSocket(
                        "/topic/notification",
                        new WebsocketReq(WebSocketCode.NOTIFICATION, "Connecting to AI Camera...")
                );
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize heartbeat message", e);
            }
        } else {
            if (kafKaHeartBeat.timeSinceLastConfirm().getSeconds() > 30) {
                log.warn("No confirmation received for 30s. Restarting heartbeat.");
                kafKaHeartBeat.start();
            }
        }
    }



}
