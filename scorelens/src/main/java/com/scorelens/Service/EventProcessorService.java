package com.scorelens.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Entity.GameSet;
import com.scorelens.Enums.ShotResult;
import com.scorelens.Enums.WebSocketTopic;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EventProcessorService {

    final Map<Integer, Boolean> gameSetStartedMap = new ConcurrentHashMap<>();
    final Map<Integer, Boolean> matchStartedMap = new ConcurrentHashMap<>();

    final GameSetService gameSetService;
    final BilliardMatchService billiardMatchService;
    final EventService eventService;
    final WebSocketService webSocketService;
    final ObjectMapper mapper;

    /**
     * Xử lý 1 Kafka message gửi về, parse thành event, log, start gameSet & match nếu cần
     * Lưu game set và match đã ongoing để check, set 1 lần, tối ưu performance
     */
    @Transactional
    public void processEvent(ProducerRequest request) {
        try {
            // Convert data (LinkedHashMap) -> LogMessageRequest
            LogMessageRequest lmr = mapper.convertValue(request.getData(), LogMessageRequest.class);

            log.info("LogMessageRequest converted: {}", lmr);

            // Push message lên WebSocket topic "/topic/logging_notification"
            webSocketService.sendToWebSocket(WebSocketTopic.NOTI_LOGGING.getValue(), lmr);

            // Lấy event detail trong log
            EventRequest event = lmr.getDetails();
            if (event == null) {
                log.warn("No event detail found in message.");
                return;
            }

            // Thêm event vào DB
            EventResponse e = eventService.addEvent(event);
            log.info("New event is added: {}", e);

            Integer gameSetID = event.getGameSetID();

            // Nếu gameSet chưa start thì start
            if (!gameSetStartedMap.containsKey(gameSetID)) {
                //update game_set status
                GameSet startedGameSet = gameSetService.startSet(gameSetID);
                log.info("Started gameSet with id: {}", startedGameSet.getGameSetID());
                gameSetStartedMap.put(gameSetID, true);

                Integer matchID = startedGameSet.getBilliardMatch().getBilliardMatchID();

                // Nếu match chưa start thì start
                if (!matchStartedMap.containsKey(matchID)) {
                    //update match status
                    String startMatchLog = billiardMatchService.startMatch(matchID);
                    log.info(startMatchLog);
                    matchStartedMap.put(matchID, true);
                }
            }

            //xử lí shot và gửi msg qua websocket
            handlingEvent(event);

        } catch (Exception ex) {
            log.error("Error while processing LOGGING message: {}", ex.getMessage());
        }
    }

    /**
     * Reset trạng thái gameSet và match đã start — dùng khi kết thúc match
     */
    public void resetGameSetAndMatchState() {
        gameSetStartedMap.clear();
        matchStartedMap.clear();
        log.info("Reset gameSetStartedMap and matchStartedMap");
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
