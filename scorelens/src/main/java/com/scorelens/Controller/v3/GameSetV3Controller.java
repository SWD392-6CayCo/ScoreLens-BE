package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.GameSetCreateRequest;
import com.scorelens.DTOs.Request.GameSetUpdateRequest;
import com.scorelens.DTOs.Request.GameSetV3Request;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.Entity.GameSet;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.GameSetService;
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
@Tag(name = "Game Set V3", description = "Unified Game Set API")
@RestController
@RequestMapping("v3/gamesets")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameSetV3Controller {
    
    @Autowired
    GameSetService gameSetService;

    @Operation(summary = "Get game sets with unified parameters", 
               description = "Unified API that combines all GET operations from v1 controller")
    @GetMapping
    public ResponseObject getGameSets(
            @Parameter(description = "Query type: all, byId, byMatch")
            @RequestParam(required = false, defaultValue = "all") String queryType,
            
            @Parameter(description = "Game Set ID (required for queryType=byId)")
            @RequestParam(required = false) Integer gameSetId,
            
            @Parameter(description = "Match ID (required for queryType=byMatch)")
            @RequestParam(required = false) Integer matchId,
            
            @Parameter(description = "Page number (1-based)")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "10") Integer size,
            
            @Parameter(description = "Sort field")
            @RequestParam(required = false, defaultValue = "gameSetNo") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection
    ) {
        try {
            Object data;
            String message;
            
            switch (queryType.toLowerCase()) {
                case "all":
                    data = gameSetService.getAllGameSets();
                    message = "Get Game Set list";
                    break;

                case "byid":
                    if (gameSetId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Game Set ID is required for queryType=byId")
                                .build();
                    }
                    data = gameSetService.getById(gameSetId);
                    message = "Get GameSet information successfully";
                    break;
                    
                case "bymatch":
                    if (matchId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Match ID is required for queryType=byMatch")
                                .build();
                    }
                    data = gameSetService.getByMatchID(matchId);
                    message = "Get Game Sets information successfully";
                    break;
                    
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid queryType. Valid values: all, byId, byMatch")
                            .build();
            }
            
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in getGameSets: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping
    public ResponseObject createSet(@RequestBody GameSetCreateRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Create GameSet sucessfully")
                .data(gameSetService.createSet(request))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject updateSet(@PathVariable Integer id, @RequestBody GameSetUpdateRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Update GameSet sucessfully")
                .data(gameSetService.updateSet(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject deleteSet(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("GameSet with ID " + id + " has been deleted")
                .data(gameSetService.delete(id))
                .build();
    }


    public ResponseObject manualUpdateSet(Integer billiardMatchID) {
        List<GameSet> list = gameSetService.getByMatch(billiardMatchID);
        for (GameSet gameSet : list) {
            if (gameSet.getGameSetNo() == 1){
                GameSet tmp = gameSetService.startSet(gameSet.getGameSetID());
            }
        }
        return ResponseObject.builder()
                .status(1000)
                .message("Manual Update GameSet successfully")
                .data(true)
                .build();
    }

}
