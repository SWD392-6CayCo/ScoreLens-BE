package com.scorelens.Controller.v3;

import com.scorelens.Controller.v1.GameSetV1Controller;
import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Enums.MatchStatus;
import com.scorelens.Service.BilliardMatchService;
import com.scorelens.Service.BilliardTableService;
import com.scorelens.Service.EventProcessorService;
import com.scorelens.Service.KafkaService.KafkaProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Slf4j
@Tag(name = "Billiard Match V3", description = "Unified Billiard Match API")
@RestController
@RequestMapping("v3/billiardmatches")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BilliardMatchV3Controller {

    @Autowired
    BilliardMatchService billiardMatchService;

    @Autowired
    BilliardTableService billiardTableService;

    @Autowired
    KafkaProducer producer;

    @Autowired
    GameSetV1Controller gameSetController;

    @Autowired
    EventProcessorService eventProcessorService;

    @Operation(summary = "Get billiard matches with unified parameters", 
               description = "Unified API that combines all GET operations from v1 controller")
    @GetMapping
    public ResponseObject getBilliardMatches(
            @Parameter(description = "Query type: byId, byCustomer, byStaff, byPlayer, byCreatorCustomer, byCreatorStaff, filter")
            @RequestParam(required = false, defaultValue = "filter") String queryType,
            
            @Parameter(description = "Match ID (required for queryType=byId)")
            @RequestParam(required = false) Integer matchId,
            
            @Parameter(description = "Customer ID (required for queryType=byCustomer or byCreatorCustomer)")
            @RequestParam(required = false) String customerId,
            
            @Parameter(description = "Staff ID (required for queryType=byStaff or byCreatorStaff)")
            @RequestParam(required = false) String staffId,
            
            @Parameter(description = "Player ID (required for queryType=byPlayer)")
            @RequestParam(required = false) Integer playerId,
            
            @Parameter(description = "Filter by date (for queryType=filter)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
            
            @Parameter(description = "Filter by status (for queryType=filter)")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Filter by mode ID (for queryType=filter)")
            @RequestParam(required = false) Integer modeID,
            
            @Parameter(description = "Page number (1-based)")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "10") Integer size,
            
            @Parameter(description = "Sort field")
            @RequestParam(required = false, defaultValue = "startTime") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDirection
    ) {
        try {
            Object data;
            String message;
            
            switch (queryType.toLowerCase()) {
                case "byid":
                    if (matchId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Match ID is required for queryType=byId")
                                .build();
                    }
                    data = billiardMatchService.getById(matchId);
                    message = "Get Match information successfully";
                    break;
                    
                case "bycustomer":
                    if (customerId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Customer ID is required for queryType=byCustomer")
                                .build();
                    }
                    data = billiardMatchService.getByCustomerID(customerId);
                    message = "Get Matches by customer successfully";
                    break;
                    
                case "bycreatorcustomer":
                    if (customerId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Customer ID is required for queryType=byCreatorCustomer")
                                .build();
                    }
                    data = billiardMatchService.getByCustomer(customerId);
                    message = "Get Matches by creator customer successfully";
                    break;
                    
                case "bystaff":
                case "bycreatorstaff":
                    if (staffId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Staff ID is required for queryType=byStaff or byCreatorStaff")
                                .build();
                    }
                    data = billiardMatchService.getByStaff(staffId);
                    message = "Get Matches by staff successfully";
                    break;
                    
                case "byplayer":
                    if (playerId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Player ID is required for queryType=byPlayer")
                                .build();
                    }
                    data = billiardMatchService.getByPlayerID(playerId);
                    message = "Get Match by player successfully";
                    break;
                    
                case "filter":
                default:
                    MatchFilterRequest filterRequest = new MatchFilterRequest();
                    filterRequest.setDate(date);
                    filterRequest.setStatus(status);
                    filterRequest.setModeID(modeID);
                    data = billiardMatchService.getFilter(filterRequest);
                    message = "Get filtered Matches successfully";
                    break;
            }
            
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in getBilliardMatches: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
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

    @Operation(summary = "Update billiard match with unified parameters",
               description = "Unified API that combines all PUT operations from v1 controller")
    @PutMapping
    public ResponseObject updateBilliardMatch(@RequestBody BilliardMatchV3UpdateRequest request) {
        try {
            String updateType = request.getUpdateType();
            if (updateType == null) {
                return ResponseObject.builder()
                        .status(400)
                        .message("updateType is required. Valid values: update, score, cancel, complete, manual")
                        .build();
            }

            switch (updateType.toLowerCase()) {
                case "update":
                    return handleUpdateMatch(request);
                case "score":
                    return handleUpdateScore(request);
                case "cancel":
                    return handleCancel(request);
                case "complete":
                    return handleComplete(request);
                case "manual":
                    return handleManualUpdate(request);
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid updateType. Valid values: update, score, cancel, complete, manual")
                            .build();
            }
        } catch (Exception e) {
            log.error("Error in updateBilliardMatch: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    private ResponseObject handleUpdateMatch(BilliardMatchV3UpdateRequest request) {
        if (request.getMatchID() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Match ID is required for update operation")
                    .build();
        }

        BilliardMatchUpdateRequest updateRequest = new BilliardMatchUpdateRequest();
        updateRequest.setWinner(request.getWinner());
        updateRequest.setStatus(request.getStatus());

        return ResponseObject.builder()
                .status(1000)
                .message("Update Match information successfully")
                .data(billiardMatchService.updateMatch(request.getMatchID(), updateRequest))
                .build();
    }

    private ResponseObject handleUpdateScore(BilliardMatchV3UpdateRequest request) {
        if (request.getMatchID() == null || request.getTeamID() == null || request.getDelta() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("matchID, teamID, and delta are required for score update")
                    .build();
        }

        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setMatchID(request.getMatchID());
        scoreRequest.setTeamID(request.getTeamID());
        scoreRequest.setDelta(request.getDelta());

        BilliardMatchResponse rs = billiardMatchService.updateScore(scoreRequest);

        //free matchID & gameSetID in queue
        eventProcessorService.resetMatchState(rs.getBilliardMatchID());
        List<Integer> gameSetIDList = rs.getSets()
                .stream()
                .map(GameSetResponse::getGameSetID)
                .toList();
        eventProcessorService.resetGameSetState(gameSetIDList);

        if (rs.getStatus().equals(MatchStatus.completed))
            //free table
            billiardTableService.setAvailable(String.valueOf(rs.getBilliardTableID()));

        return ResponseObject.builder()
                .status(1000)
                .message("Update score successfully")
                .data(rs)
                .build();
    }

    private ResponseObject handleCancel(BilliardMatchV3UpdateRequest request) {
        if (request.getMatchID() == null || request.getForfeitTeamID() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Match ID and forfeitTeamID are required for forfeit operation")
                    .build();
        }

        BilliardMatchResponse response = billiardMatchService.cancelMatch(request.getMatchID(), request.getForfeitTeamID());
        //free table
        billiardTableService.setAvailable(String.valueOf(response.getBilliardTableID()));

        return ResponseObject.builder()
                .status(1000)
                .message("Team with ID " + request.getForfeitTeamID() + " has been forfeited")
                .data(response)
                .build();
    }

    private ResponseObject handleComplete(BilliardMatchV3UpdateRequest request) {
        if (request.getMatchID() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Match ID is required for complete operation")
                    .build();
        }

        BilliardMatch match = billiardMatchService.findMatchByID(request.getMatchID());
        //free table
        billiardTableService.setAvailable(String.valueOf(match.getBillardTable().getBillardTableID()));

        return ResponseObject.builder()
                .status(1000)
                .message("Match is currently completed")
                .data(billiardMatchService.completeMatch(request.getMatchID()))
                .build();
    }

    private ResponseObject handleManualUpdate(BilliardMatchV3UpdateRequest request) {
        if (request.getMatchID() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Match ID is required for manual update operation")
                    .build();
        }

        BilliardMatch match = billiardMatchService.findMatchByID(request.getMatchID());
        String manualMatch = billiardMatchService.startMatch(match.getBilliardMatchID());
        ResponseObject tmp = gameSetController.manualUpdateSet(match.getBilliardMatchID());

        return ResponseObject.builder()
                .status(1000)
                .message("Match's information updated manually successfully")
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
