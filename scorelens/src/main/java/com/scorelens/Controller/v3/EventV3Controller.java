package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.EventRequest;
import com.scorelens.DTOs.Request.EventV3Request;
import com.scorelens.DTOs.Response.EventResponse;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.EventService;
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
@Tag(name = "Event V3", description = "Unified Event API")
@RestController
@RequestMapping("v3/events")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventV3Controller {
    
    @Autowired
    EventService eventService;

    @Operation(summary = "Get events with unified parameters", 
               description = "Unified API that combines all GET operations from v1 and v2 controllers")
    @GetMapping
    public ResponseObject getEvents(
            @Parameter(description = "Query type: all, byPlayer, byGameSet, byPlayerAndGameSet, countByGameSet")
            @RequestParam(required = false, defaultValue = "all") String queryType,
            
            @Parameter(description = "Player ID (required for queryType=byPlayer or byPlayerAndGameSet)")
            @RequestParam(required = false) Integer playerId,
            
            @Parameter(description = "Game Set ID (required for queryType=byGameSet, byPlayerAndGameSet, or countByGameSet)")
            @RequestParam(required = false) Integer gameSetId,
            
            @Parameter(description = "Page number (1-based)")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "10") Integer size,
            
            @Parameter(description = "Sort field")
            @RequestParam(required = false, defaultValue = "timeStamp") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDirection
    ) {
        try {
            Object data;
            String message;
            
            switch (queryType.toLowerCase()) {
                case "all":
                    data = eventService.getAllEvents();
                    message = "Event list";
                    break;
                    
                case "byplayer":
                    if (playerId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Player ID is required for queryType=byPlayer")
                                .build();
                    }
                    data = eventService.getEventsByPlayerID(playerId);
                    message = "Event found";
                    break;
                    
                case "bygameset":
                    if (gameSetId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Game Set ID is required for queryType=byGameSet")
                                .build();
                    }
                    data = eventService.getEventsByGameSetID(gameSetId);
                    message = "Event found";
                    break;
                    
                case "byplayerandgameset":
                    if (playerId == null || gameSetId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Both Player ID and Game Set ID are required for queryType=byPlayerAndGameSet")
                                .build();
                    }
                    data = eventService.getEventsByPlayerIDAndGameSetID(gameSetId, playerId);
                    message = "Event list";
                    break;
                    
                case "countbygameset":
                    if (gameSetId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Game Set ID is required for queryType=countByGameSet")
                                .build();
                    }
                    data = eventService.countEventsGameSetID(gameSetId);
                    message = "Events count";
                    break;
                    
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid queryType. Valid values: all, byPlayer, byGameSet, byPlayerAndGameSet, countByGameSet")
                            .build();
            }
            
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in getEvents: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }
    
    @PostMapping
    public ResponseObject addEvent(@RequestBody EventRequest event) {
        return ResponseObject.builder()
                .status(1000)
                .message("Event added")
                .data(eventService.addEvent(event))
                .build();
    }
    
    @DeleteMapping("/player/{id}")
    public ResponseObject deleteEventByPlayerID(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Events deleted")
                .data(eventService.deleteEventByPlayerID(id))
                .build();
    }
    
    @DeleteMapping("/game_set/{id}")
    public ResponseObject deleteEventByRoundID(@PathVariable int id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Events deleted")
                .data(eventService.deleteEventByGameSetID(id))
                .build();
    }
}
