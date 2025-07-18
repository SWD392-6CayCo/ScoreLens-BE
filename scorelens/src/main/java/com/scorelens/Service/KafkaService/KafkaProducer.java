package com.scorelens.Service.KafkaService;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.InformationRequest;
import com.scorelens.DTOs.Request.ProducerRequest;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.DTOs.Response.PlayerResponse;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Enums.KafkaCode;
import com.scorelens.Enums.WebSocketCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.BilliardTableService;
import com.scorelens.Service.FCMService;
import com.scorelens.Service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final int partition = 0;

    private final WebSocketService webSocketService;

    private final KafKaHeartBeat kafKaHeartBeat;

    private final HeartbeatService heartbeatService;

    private final FCMService fcmService;

    //    @Value("${spring.kafka.producer.topic}")
    private final String ja_to_py_topic = "ja_to_py";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BilliardTableService billiardTableService;


    public void sendEvent(Object object) {
        kafkaTemplate.send(ja_to_py_topic, partition, null, object);
        kafkaTemplate.flush();
        log.info("Sent kafka message: {} to topic: {}", object, ja_to_py_topic);
    }

    //send event voi key = tableID
    public void sendEvent(String tableID, Object object) {
        kafkaTemplate.send(ja_to_py_topic, tableID, object);
        kafkaTemplate.flush();
        log.info("Sent kafka message: {} to topic: {} with key: {}", object, ja_to_py_topic, tableID);
    }

    public void deleteEventByPlayer(Object object) {
        sendEvent(new ProducerRequest(KafkaCode.DELETE_PLAYER, null, object));
    }

    public void deleteEventByGameSet(Object object) {
        sendEvent(new ProducerRequest(KafkaCode.DELETE_GAME_SET, null, object));
    }

    //cứ mỗi 10s, hàm sẽ run 1 lần
    //gửi msg đến fastapi liên tục đến khi có cập nhật mới ở listener
    //k sợ miss flag vì KafkaHeartBeat đã khai báo @Component => singleton scope
//    @Scheduled(fixedDelay = 10000)
    public void sendHeartbeat(String tableID) {
        if (kafKaHeartBeat.isRunning()) {
            try {
                String message = objectMapper.writeValueAsString(new ProducerRequest(KafkaCode.RUNNING, tableID, "Heart beat checking"));
                sendEvent(tableID, message);
                webSocketService.sendToWebSocket(
                        WebSocketTopic.NOTI_NOTIFICATION.getValue() + tableID,
                        new WebsocketReq(WebSocketCode.NOTIFICATION, "Connecting to AI Camera...")
                );
                fcmService.sendNotification(
                        tableID,
                        "Connecting to AI Camera...",
                        "noti"
                );

                //sau 10s neu py k gui msg
                CompletableFuture<Boolean> future = heartbeatService.onHeartbeatChecking(tableID);
                future.thenAccept(result -> {
                    if (result) {
                        log.info("Heartbeat OK");
                    } else {
                        log.warn("Heartbeat Timeout");
                    }
                });
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize heartbeat message", e);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (kafKaHeartBeat.timeSinceLastConfirm().getSeconds() > 30) {
                log.warn("No confirmation received for 30s. Restarting heartbeat.");
                kafKaHeartBeat.start();
            }
        }
    }

    // map match_response to req
    public InformationRequest mapInformation(BilliardMatchResponse response) {
        //set code
        InformationRequest req = new InformationRequest();
        req.setCode(KafkaCode.START_STREAM);

        //set tableID
        req.setTableID(response.getBilliardTableID());

        //SET MODE ID
        req.setModeID(response.getModeID());

        //set camera url
        InformationRequest.Information info = new InformationRequest.Information();
        String cameraUrl = billiardTableService.findBilliardTableById(response.getBilliardTableID()).getCameraUrl();
        info.setCameraUrl(cameraUrl);

        //set totalSet
        info.setTotalSet(response.getTotalSet());

        //map game set
        List<InformationRequest.GameSet> gsList = new ArrayList<>();
        for (GameSetResponse g : response.getSets()) {
            InformationRequest.GameSet gs = new InformationRequest.GameSet();
            gs.setGameSetID(g.getGameSetID());
            gs.setRaceTo(g.getRaceTo());
            gsList.add(gs);
        }
        info.setSets(gsList);

        //map team and player
        List<InformationRequest.Team> teamList = getTeamList(response);
        info.setTeams(teamList);
        req.setData(info);
        return req;
    }

    private static List<InformationRequest.Team> getTeamList(BilliardMatchResponse response) {
        List<InformationRequest.Team> teamList = new ArrayList<>();

        for (TeamResponse t : response.getTeams()) {
            InformationRequest.Team tmp = new InformationRequest.Team();
            tmp.setTeamID(t.getTeamID());

            List<InformationRequest.Player> player = new ArrayList<>();
            for (PlayerResponse p : t.getPlayers()) {
                InformationRequest.Player tmpP = new InformationRequest.Player();
                tmpP.setPlayerID(p.getPlayerID());
                tmpP.setName(p.getName());
                player.add(tmpP);
            }
            tmp.setPlayers(player);
            teamList.add(tmp);
        }
        return teamList;
    }

}
