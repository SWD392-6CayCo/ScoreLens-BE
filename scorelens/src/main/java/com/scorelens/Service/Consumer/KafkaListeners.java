package com.scorelens.Service.Consumer;

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
import com.scorelens.Service.NotificationService;
import com.scorelens.Service.WebSocketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;



import java.time.LocalTime;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

    ObjectMapper mapper = new ObjectMapper();

    EventService eventService;

    WebSocketService webSocketService;

    KafKaHeartBeat kafkaHeartBeat;

    //nhận json message
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "scorelens",
                    partitions = {"0"}
            ),
            containerFactory = "StringKafkaListenerContainerFactory"
    )
    public void listenJson(String message) {
        try {
            LogMessageRequest logMessage = mapper.readValue(message, LogMessageRequest.class);
            System.out.println("Received JSON message via SSL KafkaListener:\n" + message);
            // Push message lên WebSocket topic "/topic/logging_notification"
            webSocketService.sendToWebSocket(WebSocketTopic.NOTI_LOGGING.getValue(), message);
            // lấy event
            EventRequest event = logMessage.getDetails();
            //tạo mới 1 event theo player và round
            EventResponse e = eventService.addEvent(event);
            log.info("New event is added: {}", e);
            //xử lí shot và gửi msg qua websocket
            handlingEvent(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // nhận heart beat từ fastapi
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "ping",
                    partitions = {"0"}
            ),
            containerFactory = "StringKafkaListenerContainerFactory"
    )
    public void listenCommunication(String message) {
        try {
            ProducerRequest request = mapper.readValue(message, ProducerRequest.class);
            System.out.println("Received Communication via SSL KafkaListener:\n" + message);

            handlingKafkaCode(request.getCode());

        } catch (JsonProcessingException e) {
            log.error("Failed to parse ProducerRequest: {}", message, e);
        }
    }

    // tạo event mới và xác định shot event
    public void handlingEvent(EventRequest request) {
        boolean isFoul = request.isFoul();
        boolean scoreValue = request.isScoreValue();
        boolean isUncertain = request.isUncertain();
        //lấy ds event theo round để đếm số shot đã đánh
        int tmp = eventService.countEventsGameSetID(request.getGameSetID());
        int shotCount = tmp == 0 ?  1 : tmp;

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

    //xử lí enum KafkaCode
    public void handlingKafkaCode(KafkaCode code){
        try {
            switch (code){
                case RUNNING:
                    kafkaHeartBeat.stop();
                    kafkaHeartBeat.updateLastConfirmedTime();
                    webSocketService.sendToWebSocket(
                            "/topic/notification",
                            new WebsocketReq(WebSocketCode.NOTIFICATION, "AI Camera Connected")
                    );
                    break;

                default:
                    System.out.println("Unknown code.");

            }
        } catch (IllegalArgumentException e){
            System.out.println("Invalid KafkaCode: " + code);
        }
    }



}
