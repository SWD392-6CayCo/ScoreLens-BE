package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.BilliardTableRequest;
import com.scorelens.DTOs.Request.BilliardTableV3UpdateRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.DTOs.Response.BilliardTableResponse;
import com.scorelens.DTOs.Response.StoreResponse;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Enums.TableStatus;
import com.scorelens.Service.BilliardMatchService;
import com.scorelens.Service.BilliardTableService;
import com.scorelens.Service.StoreService;
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
@Tag(name = "Billiard Table V3", description = "Unified Billiard Table API")
@RestController
@RequestMapping("v3/tables")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BilliardTableV3Controller {
    
    @Autowired
    BilliardTableService billiardTableService;
    
    @Autowired
    StoreService storeService;
    
    @Autowired
    BilliardMatchService billiardMatchService;

    @Operation(summary = "Get billiard tables with unified parameters", 
               description = "Unified API that combines all GET operations from v1 controller")
    @GetMapping
    public ResponseObject getBilliardTables(
            @Parameter(description = "Query type: all, byId, byStore")
            @RequestParam(required = false, defaultValue = "all") String queryType,
            
            @Parameter(description = "Table ID (required for queryType=byId)")
            @RequestParam(required = false) String tableId,
            
            @Parameter(description = "Store ID (required for queryType=byStore)")
            @RequestParam(required = false) String storeId,
            
            @Parameter(description = "Page number (1-based)")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "10") Integer size,
            
            @Parameter(description = "Sort field")
            @RequestParam(required = false, defaultValue = "tableCode") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection
    ) {
        try {
            Object data;
            String message;
            
            switch (queryType.toLowerCase()) {
                case "all":
                    data = billiardTableService.getAllBilliardTables();
                    message = "All tables";
                    break;
                    
                case "byid":
                    if (tableId == null || tableId.trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Table ID is required for queryType=byId")
                                .build();
                    }
                    BilliardTableResponse rs = billiardTableService.findBilliardTableById(tableId);
                    // Add match information if table is in use (like in V2 controller)
                    if (rs.getStatus().equals(TableStatus.inUse)) {
                        BilliardMatchResponse tmp = billiardMatchService.getOnGoingMatch(rs.getBillardTableID());
                        if (tmp != null) {
                            rs.setMatchResponse(tmp);
                        }
                    }
                    data = rs;
                    message = "Table with id " + tableId;
                    break;
                    
                case "bystore":
                    if (storeId == null || storeId.trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Store ID is required for queryType=byStore")
                                .build();
                    }
                    StoreResponse store = storeService.findStoreById(storeId);
                    List<BilliardTableResponse> response = billiardTableService.getTablesByStore(storeId);
                    // Add match information if table is in use (like in V2 controller)
                    for (BilliardTableResponse table : response) {
                        if (table.getStatus().equals(TableStatus.inUse)) {
                            BilliardMatchResponse tmp = billiardMatchService.getOnGoingMatch(table.getBillardTableID());
                            if (tmp != null) {
                                table.setMatchResponse(tmp);
                            }
                        }
                    }
                    data = response;
                    message = "Tables in Store name:" + store.getName();
                    break;
                    
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid queryType. Valid values: all, byId, byStore")
                            .build();
            }
            
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in getBilliardTables: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }
    
    @Operation(summary = "Update billiard table with unified parameters", 
               description = "Unified API that combines all PUT operations from v1 controller")
    @PutMapping
    public ResponseObject updateBilliardTable(@RequestBody BilliardTableV3UpdateRequest request) {
        try {
            String updateType = request.getUpdateType();
            if (updateType == null) {
                return ResponseObject.builder()
                        .status(400)
                        .message("updateType is required. Valid values: update, status")
                        .build();
            }
            
            if (request.getId() == null || request.getId().trim().isEmpty()) {
                return ResponseObject.builder()
                        .status(400)
                        .message("Table ID is required")
                        .build();
            }
            
            switch (updateType.toLowerCase()) {
                case "update":
                    return handleUpdateTable(request);
                case "status":
                    return handleUpdateStatus(request);
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid updateType. Valid values: update, status")
                            .build();
            }
        } catch (Exception e) {
            log.error("Error in updateBilliardTable: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }
    
    private ResponseObject handleUpdateTable(BilliardTableV3UpdateRequest request) {
        BilliardTableRequest tableRequest = new BilliardTableRequest();
        tableRequest.setTableType(request.getTableType());
        tableRequest.setName(request.getName());
        tableRequest.setDescription(request.getDescription());
        tableRequest.setStatus(request.getStatus());
        tableRequest.setCameraUrl(request.getCameraUrl());
        tableRequest.setActive(request.isActive());
        tableRequest.setStoreID(request.getStoreID());
        
        return ResponseObject.builder()
                .status(1000)
                .message("Table is updated")
                .data(billiardTableService.updateBilliardTable(request.getId(), tableRequest))
                .build();
    }
    
    private ResponseObject handleUpdateStatus(BilliardTableV3UpdateRequest request) {
        if (request.getStatusValue() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Status value is required for status update operation")
                    .build();
        }
        
        return ResponseObject.builder()
                .status(1000)
                .message("Table is updated")
                .data(billiardTableService.updateBilliardTable(request.getId(), request.getStatusValue()))
                .build();
    }
    
    @PostMapping
    public ResponseObject addTable(@RequestBody BilliardTableRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("New table is created")
                .data(billiardTableService.createBilliardTable(request))
                .build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseObject deleteTable(@PathVariable String id) {
        String tableCode = billiardTableService.findBilliardTableById(id).getTableCode();
        log.info("Table {} is deleted", tableCode);
        return ResponseObject.builder()
                .status(1000)
                .message(String.format("Table with code %s is deleted", tableCode))
                .data(billiardTableService.deleteBilliardTable(id))
                .build();
    }
}
