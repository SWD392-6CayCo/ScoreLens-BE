package com.scorelens.Controller.v1;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.DTOs.Response.NotificationResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Enums.MatchStatus;
import com.scorelens.Enums.NotificationType;
import com.scorelens.Service.BilliardMatchService;
import com.scorelens.Service.BilliardTableService;
import com.scorelens.Service.EventProcessorService;
import com.scorelens.Service.NotificationService;
import com.scorelens.Service.KafkaService.KafkaProducer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Billiard Match", description = "Manage Billiard Match")
@RestController
@RequestMapping("v1/billiard-matches")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BilliardMatchV1Controller {
    BilliardMatchService billiardMatchService;

    BilliardTableService billiardTableService;

    KafkaProducer producer;

    GameSetV1Controller gameSetController;

    EventProcessorService eventProcessorService;

    NotificationService notificationService;


    @GetMapping("/{id}")
    public ResponseObject getById(@PathVariable Integer id) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Get Match information successfully")
                        .data(billiardMatchService.getById(id))
                        .build();
    }

    @GetMapping("/filtering")
    public ResponseObject getFilter(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) Integer modeID
    ) {
        MatchFilterRequest request = new MatchFilterRequest();
        request.setDate(date);
        request.setStatus(status);
        request.setModeID(modeID);
        return ResponseObject.builder()
                .status(1000)
                .message("Get Matchs information successfully")
                .data(billiardMatchService.getFilter(request))
                .build();
    }

    @GetMapping("/bycreator/customer/{id}")
    public ResponseObject getByCustomer(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Matchs information successfully")
                .data(billiardMatchService.getByCustomer(id))
                .build();
    }

    @GetMapping("/bycreator/staff/{id}")
    public ResponseObject getByStaff(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Matchs information successfully")
                .data(billiardMatchService.getByStaff(id))
                .build();
    }

    @GetMapping("/bycustomer/{id}")
    public ResponseObject getByCustomerID(@PathVariable String id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Matchs information successfully")
                .data(billiardMatchService.getByCustomerID(id))
                .build();
    }

    @GetMapping("/byplayer/{id}")
    public ResponseObject getByPlayerID(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Get Match information successfully")
                .data(billiardMatchService.getByPlayerID(id))
                .build();
    }

    @PostMapping
    public ResponseObject createMatch(@RequestBody BilliardMatchCreateRequest request) {
        BilliardMatchResponse response = billiardMatchService.createMatch(request);
        String tableID = response.getBilliardTableID();
        //cam ai check
        producer.sendHeartbeat(tableID);

        //gửi thông tin trận đấu cho py
        InformationRequest req = producer.mapInformation(response);
        producer.sendEvent(tableID, req);

        //set table status: inUse
        billiardTableService.setInUse(String.valueOf(response.getBilliardTableID()));
        return ResponseObject.builder()
                .status(1000)
                .message("Create new Match successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject updateMatch(@PathVariable Integer id, @RequestBody BilliardMatchUpdateRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Update Match information successfully")
                .data(billiardMatchService.updateMatch(id,request))
                .build();
    }

    @PutMapping("/score")
    public ResponseObject updateScore(@RequestBody ScoreRequest request) throws FirebaseMessagingException {
        BilliardMatchResponse rs = billiardMatchService.updateScore(request);

        //free matchID & gameSetID in queue
        eventProcessorService.resetMatchState(rs.getBilliardMatchID());
        List<Integer> gameSetIDList = rs.getSets()
                .stream()
                .map(GameSetResponse::getGameSetID)
                .toList();
        eventProcessorService.resetGameSetState(gameSetIDList);

        // Add notification for score update
        NotificationResponse tmp = newNoti(
                request.getMatchID(),
                "",
                NotificationType.score
        );

        if (rs.getStatus().equals(MatchStatus.completed))
            //free table
            billiardTableService.setAvailable(String.valueOf(rs.getBilliardMatchID()));
        return ResponseObject.builder()
                .status(1000)
                .message("Update score successfully")
                .data(rs)
                .build();
    }

    @PutMapping("/cancel/{id}")
    public ResponseObject forfeit(@PathVariable Integer id, @RequestBody Integer teamID) {
        return ResponseObject.builder()
                .status(1000)
                .message("Team with ID " + teamID + " has been forfeited")
                .data(billiardMatchService.cancelMatch(id,teamID))
                .build();
    }

//    @PutMapping("/cancel/{id}")
//    public ResponseObject cancel(@PathVariable Integer id) {
//        BilliardMatchResponse response = billiardMatchService.cancel(id);
//        //free table
//        billiardTableService.setAvailable(String.valueOf(response.getBilliardMatchID()));
//        return ResponseObject.builder()
//                .status(1000)
//                .message("Cancel Match successfully")
//                .data(billiardMatchService.cancel(id))
//                .build();
//    }

    @PutMapping("/complete/{id}")
    public ResponseObject complete(@PathVariable Integer id) {
        BilliardMatch match = billiardMatchService.findMatchByID(id);
        //free table
        billiardTableService.setAvailable(String.valueOf(match.getBilliardMatchID()));
        return ResponseObject.builder()
                .status(1000)
                .message("Match is currently completed")
                .data(billiardMatchService.completeMatch(id))
                .build();
    }

    @PutMapping("manual/{id}")
    public ResponseObject manualUpdateMatch(@PathVariable Integer id) {
        BilliardMatch match = billiardMatchService.findMatchByID(id);
        String manualMatch = billiardMatchService.startMatch(match.getBilliardMatchID());
        ResponseObject tmp = gameSetController.manualUpdateSet(match.getBilliardMatchID());
        return ResponseObject.builder()
                .status(1000)
                .message("Match's information manually successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject deleteMatch(@PathVariable Integer id) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Match with ID " + id + " has been deleted")
                        .data(billiardMatchService.delete(id))
                        .build();
    }

    @DeleteMapping()
    public void deleteAll() {
        billiardMatchService.deleteAll();
    }

    private NotificationResponse newNoti(int matchID, String msg, NotificationType type) {
        //add info into notification
        return notificationService.saveNotification(
                new NotificationRequest(
                        matchID,
                        msg,
                        type
                ));
    }
}

