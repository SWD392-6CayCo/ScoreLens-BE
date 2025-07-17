package com.scorelens.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Entity.GameSet;
import com.scorelens.Entity.Player;
import com.scorelens.Enums.ShotResult;
import com.scorelens.Enums.WSFCMCode;
import com.scorelens.Enums.WebSocketTopic;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
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
    final FCMService fcmService;
    final BilliardMatchService matchService;
    final PlayerService playerService;

    /**
     * Xử lý 1 Kafka message gửi về, parse thành event, log, start gameSet & match nếu cần
     * Lưu game set và match đã ongoing để check, set 1 lần, tối ưu performance
     */
    @Transactional
    public void processEvent(ProducerRequest request) {
        String tableID = request.getTableID();
        try {
            // Convert data (LinkedHashMap) -> LogMessageRequest
            LogMessageRequest lmr = mapper.convertValue(request.getData(), LogMessageRequest.class);

            log.info("LogMessageRequest converted: {}", lmr);

            // Push message lên WebSocket topic "/topic/logging_notification"
            webSocketService.sendToWebSocket(
                    WebSocketTopic.NOTI_LOGGING.getValue() + tableID,
                    new WebsocketReq(WSFCMCode.LOGGING, lmr)
            );

            // Lấy event detail trong log
            EventRequest event = lmr.getDetails();
            if (event == null) {
                log.warn("No event detail found in message.");
                return;
            }

            Integer gameSetID = event.getGameSetID();

            //xử lí shot và gửi msg qua websocket
            handlingEvent(event, tableID);

            // Thêm event vào DB
            EventResponse e = eventService.addEvent(event);
            log.info("New event is added: {}", e);

            //1 round đấu kết thúc => update match score
            //bi 9 potted && !isFoul && scored
            if (event.isScoreValue() && !event.isFoul() && lmr.getTargetBallId() == 9) {
                //update match score
                updateMatch(event.getPlayerID());
            }

            // Nếu gameSet chưa start thì start
            if (!gameSetStartedMap.containsKey(gameSetID)) {
                //update game_set status
                GameSet startedGameSet = gameSetService.startSet(gameSetID);
                log.info("Started gameSet with id: {}", startedGameSet.getGameSetID());
                //set ongoing cho từng gameSet
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

        } catch (Exception ex) {
            log.error("Error while processing LOGGING message: {}", ex.getMessage());
        }
    }

    private void updateMatch(int playerID) {
        Player player = playerService.getPlayer(playerID);

        matchService.updateScore(
                new ScoreRequest(
                        player.getTeam().getBilliardMatch().getBilliardMatchID(),
                        player.getTeam().getTeamID(),
                        "+1"
                ));
    }

    /**
     * Reset trạng thái gameSet và match đã start — dùng khi kết thúc match
     */
    public void resetGameSetState(List<Integer> gameSetIDs) {
        gameSetIDs.forEach(id -> {
            gameSetStartedMap.remove(id);
            log.info("Removed gameSetID: {} from gameSetStartedMap", id);
        });
    }


    public void resetMatchState(int matchID) {
        matchStartedMap.remove(matchID);
        log.info("Removed matchID: {} from matchStartedMap", matchID);
    }


    // xác định shot event
    public void handlingEvent(EventRequest request, String tableID) throws FirebaseMessagingException {
        boolean isFoul = request.isFoul();
        boolean scoreValue = request.isScoreValue();
        boolean isUncertain = request.isUncertain();
        //lấy ds event theo gameset để đếm số shot đã đánh
        int tmp = eventService.countEventsGameSetID(request.getGameSetID());
        int shotCount = tmp == 0 ? 1 : tmp;

        ShotEvent shot = new ShotEvent();
        // nếu AI k chắc chắn => undetected
        ShotResult result = !isUncertain && !isFoul && !scoreValue ? ShotResult.MISSED
                : isUncertain ? ShotResult.UNDETECTED
                : scoreValue ? ShotResult.SCORED
                : ShotResult.FOUL;

        shot.setTime(LocalTime.now());
        shot.setShot(String.format("SHOT #%02d", shotCount));
        shot.setPlayer(String.format("PLAYER %d", request.getPlayerID()));
        shot.setResult(result.name());

//        gửi thông báo qua web socket bằng topic: shot_event
        webSocketService.sendToWebSocket(
                WebSocketTopic.NOTI_SHOT.getValue() + tableID,
                new WebsocketReq(WSFCMCode.SHOT, shot)
        );
        fcmService.sendNotification(
                tableID,
                String.valueOf(WSFCMCode.SHOT),
                String.valueOf(shot)
        );

        log.info("Send websocket to: /topic/shot_event/" + tableID);
        log.info(shot.toString());

    }


}
