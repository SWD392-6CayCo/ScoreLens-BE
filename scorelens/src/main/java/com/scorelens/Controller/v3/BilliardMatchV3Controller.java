package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.BilliardMatchV3Request;
import com.scorelens.DTOs.Request.MatchFilterRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.BilliardMatchService;
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
}
