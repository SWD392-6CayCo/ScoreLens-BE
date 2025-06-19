package com.scorelens.Service.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.DTOs.Request.LogMessageRequest;
import com.scorelens.DTOs.Request.ShotEvent;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Enums.ShotResult;
import com.scorelens.Service.EventService;
import com.scorelens.Service.NotificationService;
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

    private final NotificationService notificationService;

    private final ObjectMapper mapper = new ObjectMapper();

    EventService eventService;

    SimpMessagingTemplate messagingTemplate;

    // json message
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "scorelens",
                    partitions = {"0"}
            ),
            containerFactory = "jsonKafkaListenerContainerFactory"
    )
    public void listenJson(LogMessageRequest message) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
            System.out.println("Received JSON message via SSL KafkaListener:\n" + json);
            // Push message lên WebSocket topic "/topic/logging_notification"
            notificationService.sendToWebSocket("/topic/logging_notification", json);
            // lấy event
            EventRequest event = message.getDetails();
            //tạo mới 1 event theo player và round
            EventResponse e = eventService.addEvent(event);
            log.info("New event is added: ", e);
            //xử lí shot và gửi msg qua websocket
            handlingEvent(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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

//        gửi thông báo qua kafka bằng topic: shot_event
        messagingTemplate.convertAndSend("/topic/shot_event", shot);
    }



}
