package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.TeamV3Request;
import com.scorelens.DTOs.Response.TeamResponse;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.TeamService;
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
@Tag(name = "Team V3", description = "Unified Team API")
@RestController
@RequestMapping("v3/teams")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeamV3Controller {
    
    @Autowired
    TeamService teamService;

    @Operation(summary = "Get teams with unified parameters", 
               description = "Unified API that combines all GET operations from v1 controller")
    @GetMapping
    public ResponseObject getTeams(
            @Parameter(description = "Query type: byId, byMatch")
            @RequestParam(required = false, defaultValue = "byId") String queryType,
            
            @Parameter(description = "Team ID (required for queryType=byId)")
            @RequestParam(required = false) Integer teamId,
            
            @Parameter(description = "Match ID (required for queryType=byMatch)")
            @RequestParam(required = false) Integer matchId,
            
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
                case "byid":
                    if (teamId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Team ID is required for queryType=byId")
                                .build();
                    }
                    data = teamService.getById(teamId);
                    message = "Get Team information successfully";
                    break;
                    
                case "bymatch":
                    if (matchId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Match ID is required for queryType=byMatch")
                                .build();
                    }
                    data = teamService.getByMatchID(matchId);
                    message = "Get Teams information successfully";
                    break;
                    
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid queryType. Valid values: byId, byMatch")
                            .build();
            }
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in getTeams: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }
}
