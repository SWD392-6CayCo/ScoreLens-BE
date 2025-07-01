package com.scorelens.Service.KafkaService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.*;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


import java.util.concurrent.CompletableFuture;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

    ObjectMapper mapper = new ObjectMapper();

    WebSocketService webSocketService;

    KafKaHeartBeat kafkaHeartBeat;

    HeartbeatService heartbeatService;

    EventProcessorService eventProcessorService;

    // msg từ fastapi
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "py_to_ja",
                    partitions = {"0"}
            ),
            containerFactory = "StringKafkaListenerContainerFactory"
    )
    public void listenCommunication(String message, Acknowledgment ack) {
        try {
            ProducerRequest request = mapper.readValue(message, ProducerRequest.class);
            System.out.println("Received Communication via SSL KafkaListener:\n" + message);
            log.info("KafkaCode received: {}", request.getCode());
            log.info("Data type: {}", request.getData().getClass());

            handlingKafkaCode(request);

            ack.acknowledge(); // commit offset sau khi xử lý xong
        } catch (JsonProcessingException e) {
            log.error("Failed to parse ProducerRequest: {}", message, e);
        }
    }

    //xử lí enum KafkaCode
    private void handlingKafkaCode(ProducerRequest request) {
        KafkaCode code = request.getCode();
        try {
            switch (code) {
                case RUNNING:
                    kafkaHeartBeat.stop();
                    kafkaHeartBeat.updateLastConfirmedTime();
                    //xac nhan da nhan res tu py
                    CompletableFuture<Boolean> tmp = heartbeatService.confirmHeartbeat();
                    log.info("CompletableFuture: {}", tmp);
                    webSocketService.sendToWebSocket(
                            "/topic/notification",
                            new WebsocketReq(WebSocketCode.NOTIFICATION, "AI Camera Connected")
                    );
                    break;
                case LOGGING:
                    eventProcessorService.processEvent(request);
                    break;
                case DELETE_CONFIRM:
                    int deleteCount = (Integer) request.getData();
                    webSocketService.sendToWebSocket(
                            "/topic/notification",
                            new WebsocketReq(WebSocketCode.WARNING, "Delete Event count: " + deleteCount));
                    break;




                default:
                    System.out.println("Unknown code.");

            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid KafkaCode: " + code);
        }
    }


}
