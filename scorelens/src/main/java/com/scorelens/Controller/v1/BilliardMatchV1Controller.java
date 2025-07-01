package com.scorelens.Controller.v1;

import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Enums.MatchStatus;
import com.scorelens.Service.BilliardMatchService;
import com.scorelens.Service.BilliardTableService;
import com.scorelens.Service.Consumer.KafkaProducer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Billiard Match", description = "Manage Billiard Match")
@RestController
@RequestMapping("v1/billiardmatches")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BilliardMatchV1Controller {

    @Autowired
    BilliardMatchService billiardMatchService;

    BilliardTableService billiardTableService;

    KafkaProducer producer;


    @GetMapping("/{id}")
    public ResponseObject getById(@PathVariable Integer id) {
        return ResponseObject.builder()
                        .status(1000)
                        .message("Get Match information successfully")
                        .data(billiardMatchService.getById(id))
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
        //cam ai check
        producer.sendHeartbeat();

        //gửi thông tin trận đấu cho py
        InformationRequest req = producer.receiveInfomation(response);
        producer.sendEvent(req);

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
    public ResponseObject updateScore(@RequestBody ScoreRequest request) {
        BilliardMatchResponse rs = billiardMatchService.updateScore(request);
        if (rs.getStatus().equals(MatchStatus.completed))
            //free table
            billiardTableService.setAvailable(String.valueOf(rs.getBilliardMatchID()));
        return ResponseObject.builder()
                .status(1000)
                .message("Update score successfully")
                .data(rs)
                .build();
    }

    @PutMapping("/forfeit/{id}")
    public ResponseObject forfeit(@PathVariable Integer id, @RequestBody Integer teamID) {
        return ResponseObject.builder()
                .status(1000)
                .message("Team with ID " + teamID + " has been forfeited")
                .data(billiardMatchService.forfeit(id,teamID))
                .build();
    }

    @PutMapping("/cancel/{id}")
    public ResponseObject cancel(@PathVariable Integer id) {
        BilliardMatchResponse response = billiardMatchService.cancel(id);
        //free table
        billiardTableService.setAvailable(String.valueOf(response.getBilliardMatchID()));
        return ResponseObject.builder()
                .status(1000)
                .message("Cancel Match successfully")
                .data(billiardMatchService.cancel(id))
                .build();
    }

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
}

