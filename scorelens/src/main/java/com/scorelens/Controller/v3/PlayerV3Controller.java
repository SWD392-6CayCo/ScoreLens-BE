package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.PlayerV3Request;
import com.scorelens.DTOs.Response.PlayerResponse;
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

import java.util.List;

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
            @Parameter(description = "Query type: all, byId")
            @RequestParam(required = false, defaultValue = "all") String queryType,
            
            @Parameter(description = "Player ID (required for queryType=byId)")
            @RequestParam(required = false) Integer playerId,
            
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
                    message = "Get player list";
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
                    
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid queryType. Valid values: all, byId")
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
}
