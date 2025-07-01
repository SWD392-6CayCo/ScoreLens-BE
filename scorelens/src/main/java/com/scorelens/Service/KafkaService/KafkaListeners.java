package com.scorelens.Service.KafkaService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.ShotResult;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.EventService;
import com.scorelens.Service.WebSocketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

    ObjectMapper mapper = new ObjectMapper();

    EventService eventService;

    WebSocketService webSocketService;

    KafKaHeartBeat kafkaHeartBeat;

    HeartbeatService heartbeatService;

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

            handlingKafkaCode(request, ack);

            ack.acknowledge(); // commit offset sau khi xử lý xong
        } catch (JsonProcessingException e) {
            log.error("Failed to parse ProducerRequest: {}", message, e);
        }
    }

    //xử lí enum KafkaCode
    private void handlingKafkaCode(ProducerRequest request, Acknowledgment ack) {
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
                    try {
                        // Convert data (LinkedHashMap) -> LogMessageRequest
                        LogMessageRequest lmr = mapper.convertValue(request.getData(), LogMessageRequest.class);
                        ack.acknowledge(); // commit offset sau khi xử lý xong
                        log.info("LogMessageRequest converted: {}", lmr);
                        // Push message lên WebSocket topic "/topic/logging_notification"
                        webSocketService.sendToWebSocket(WebSocketTopic.NOTI_LOGGING.getValue(), request);
                        // lấy event
                        EventRequest event = lmr.getDetails();
                        if (event != null) {
                            //tạo mới 1 event theo player và gameset
                            EventResponse e = eventService.addEvent(event);
                            log.info("New event is added: {}", e);
                            //xử lí shot và gửi msg qua websocket
                            handlingEvent(event);
                        } else {
                            log.warn("No event details found in LogMessageRequest.");
                        }
                    } catch (Exception e) {
                        log.error("Error while processing LOGGING message: {}", e.getMessage(), e);
                    }
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

    // xác định shot event
    public void handlingEvent(EventRequest request) {
        boolean isFoul = request.isFoul();
        boolean scoreValue = request.isScoreValue();
        boolean isUncertain = request.isUncertain();
        //lấy ds event theo gameset để đếm số shot đã đánh
        int tmp = eventService.countEventsGameSetID(request.getGameSetID());
        int shotCount = tmp == 0 ? 1 : tmp;

        ShotEvent shot = new ShotEvent();
        // nếu AI k chắc chắn => undetected
        ShotResult result = isUncertain ? ShotResult.UNDETECTED
                // AI chắc chắn shot đánh foul
                : (isFoul ? ShotResult.MISSED
                // AI chắc chắn shot scored
                : (scoreValue ? ShotResult.SCORED
                // nếu không xác định
                : ShotResult.UNKNOWN));

        shot.setTime(LocalTime.now());
        shot.setShot(String.format("SHOT #%02d", shotCount));
        shot.setPlayer(String.format("PLAYER %d", request.getPlayerID()));
        shot.setResult(result.name());

//        gửi thông báo qua web socket bằng topic: shot_event
        webSocketService.sendToWebSocket(WebSocketTopic.NOTI_SHOT.getValue(), shot);
    }



}
