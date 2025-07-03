package com.scorelens.Service.KafkaService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.*;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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
            topics = "py_to_ja",
            containerFactory = "StringKafkaListenerContainerFactory"
    )
    public void listenCommunication(
            String message,                                                                           // value
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,                  // key
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            Acknowledgment ack
    ) {
        try {
            ProducerRequest request = mapper.readValue(message, ProducerRequest.class);
            log.info("Received message on partition {} with key {}: {}", partition, key, message);
            log.info("KafkaCode received: {}", request.getCode());
            log.info("Data type: {}", request.getData().getClass());

            handlingKafkaCode(request);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse ProducerRequest: {}", message, e);
        }
    }


    //xử lí enum KafkaCode
    private void handlingKafkaCode(ProducerRequest request) {
        KafkaCode code = request.getCode();
        String tableID = request.getTableID();
        try {
            switch (code) {
                case RUNNING:
                    kafkaHeartBeat.stop();
                    kafkaHeartBeat.updateLastConfirmedTime();
                    //xac nhan da nhan res tu py
                    CompletableFuture<Boolean> tmp = heartbeatService.confirmHeartbeat();

                    log.info("CompletableFuture: {}", tmp);
                    webSocketService.sendToWebSocket(
                            WebSocketTopic.NOTI_NOTIFICATION.getValue() + tableID,
                            new WebsocketReq(WebSocketCode.NOTIFICATION, "AI Camera Connected")
                    );
                    break;
                case LOGGING:
                    eventProcessorService.processEvent(request);
                    break;
                case DELETE_CONFIRM:
                    int deleteCount = (Integer) request.getData();
                    webSocketService.sendToWebSocket(
                            WebSocketTopic.NOTI_NOTIFICATION.getValue() + tableID,
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
