package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.PlayerCreateRequest;
import com.scorelens.DTOs.Request.PlayerV3UpdateRequest;
import com.scorelens.DTOs.Request.PlayerUpdateRequest;
import com.scorelens.DTOs.Request.CustomerSaveRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Player V3", description = "Unified Player API")
@RestController
@RequestMapping("v3/players")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerV3Controller {
    
    @Autowired
    PlayerService playerService;

    @Operation(summary = "Get players with unified parameters", 
               description = "Unified API that combines all GET operations from v1 controller")
    @GetMapping
    public ResponseObject getPlayers(
            @Parameter(description = "Query type: all, byId, byTeam")
            @RequestParam(required = false, defaultValue = "all") String queryType,
            
            @Parameter(description = "Player ID (required for queryType=byId)")
            @RequestParam(required = false) Integer playerId,

            @Parameter(description = "Team ID (required for queryType=byTeam)")
            @RequestParam(required = false) Integer teamId,
            
            @Parameter(description = "Page number (1-based)")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "10") Integer size,
            
            @Parameter(description = "Sort field")
            @RequestParam(required = false, defaultValue = "createAt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDirection
    ) {
        try {
            Object data;
            String message;
            
            switch (queryType.toLowerCase()) {
                case "all":
                    data = playerService.getAllPlayers();
                    message = "Get Player list";
                    break;
                    
                case "byid":
                    if (playerId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Player ID is required for queryType=byId")
                                .build();
                    }
                    data = playerService.getPlayerById(playerId);
                    message = "Get player successfully";
                    break;

                case "byteam":
                    if (teamId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Team ID is required for queryType=byTeam")
                                .build();
                    }
                    data = playerService.getByTeam(teamId);
                    message = "Get Players information successfully";
                    break;
                    
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid queryType. Valid values: all, byId, byTeam")
                            .build();
            }
            
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in getPlayers: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping
    public ResponseObject createPlayer(@RequestBody PlayerCreateRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Create new Player successfully")
                .data(playerService.createPlayer(request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject deletePlayer(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Player with ID " + id + " has been deleted")
                .data(playerService.delete(id))
                .build();
    }

    @Operation(summary = "Update player with unified parameters",
               description = "Unified API that combines all PUT operations from v1 controller")
    @PutMapping
    public ResponseObject updatePlayer(@RequestBody PlayerV3UpdateRequest request) {
        try {
            String updateType = request.getUpdateType();
            if (updateType == null) {
                return ResponseObject.builder()
                        .status(400)
                        .message("updateType is required. Valid values: update, saveCustomer")
                        .build();
            }

            if (request.getId() == null) {
                return ResponseObject.builder()
                        .status(400)
                        .message("Player ID is required")
                        .build();
            }

            switch (updateType.toLowerCase()) {
                case "update":
                    return handleUpdatePlayer(request);
                case "savecustomer":
                    return handleSaveCustomer(request);
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid updateType. Valid values: update, saveCustomer")
                            .build();
            }
        } catch (Exception e) {
            log.error("Error in updatePlayer: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    private ResponseObject handleUpdatePlayer(PlayerV3UpdateRequest request) {
        PlayerUpdateRequest updateRequest = new PlayerUpdateRequest();
        updateRequest.setName(request.getName());
        updateRequest.setTotalScore(request.getTotalScore() != null ? request.getTotalScore() : 0);
        updateRequest.setStatus(request.getStatus());
        updateRequest.setCustomerID(request.getCustomerID());

        return ResponseObject.builder()
                .status(1000)
                .message("Update Player information successfully")
                .data(playerService.updatePlayer(request.getId(), updateRequest))
                .build();
    }

    private ResponseObject handleSaveCustomer(PlayerV3UpdateRequest request) {
        if (request.getInfo() == null || request.getInfo().trim().isEmpty()) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Customer info (email or phone number) is required for saveCustomer operation")
                    .build();
        }

        CustomerSaveRequest saveRequest = new CustomerSaveRequest();
        saveRequest.setInfo(request.getInfo());

        return ResponseObject.builder()
                .status(1000)
                .message("Update Player information successfully")
                .data(playerService.updateCustomer(request.getId(), saveRequest))
                .build();
    }
}
