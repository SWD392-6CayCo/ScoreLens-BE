package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.StoreRequest;
import com.scorelens.DTOs.Request.StoreV3UpdateRequest;
import com.scorelens.Entity.ResponseObject;
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

@Slf4j
@Tag(name = "Store V3", description = "Unified Store API")
@RestController
@RequestMapping("v3/stores")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoreV3Controller {
    
    @Autowired
    StoreService storeService;

    @Operation(summary = "Get stores with unified parameters", 
               description = "Unified API that combines all GET operations from v1 controller")
    @GetMapping
    public ResponseObject getStores(
            @Parameter(description = "Query type: all, byId")
            @RequestParam(required = false, defaultValue = "all") String queryType,
            
            @Parameter(description = "Store ID (required for queryType=byId)")
            @RequestParam(required = false) String storeId,
            
            @Parameter(description = "Page number (1-based)")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "10") Integer size,
            
            @Parameter(description = "Sort field")
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection
    ) {
        try {
            Object data;
            String message;
            
            switch (queryType.toLowerCase()) {
                case "all":
                    data = storeService.findAllStores();
                    message = "All Stores";
                    break;
                    
                case "byid":
                    if (storeId == null || storeId.trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Store ID is required for queryType=byId")
                                .build();
                    }
                    data = storeService.findStoreById(storeId);
                    message = "Store found";
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
            log.error("Error in getStores: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping
    public ResponseObject addStore(@RequestBody StoreRequest storeRequest) {
        return ResponseObject.builder()
                .status(1000)
                .message("New Store is created")
                .data(storeService.createStore(storeRequest))
                .build();
    }
    
    @Operation(summary = "Update store with unified parameters", 
               description = "Unified API that combines all PUT operations from v1 controller")
    @PutMapping
    public ResponseObject updateStore(@RequestBody StoreV3UpdateRequest request) {
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
                        .message("Store ID is required")
                        .build();
            }
            
            switch (updateType.toLowerCase()) {
                case "update":
                    return handleUpdateStore(request);
                case "status":
                    return handleUpdateStatus(request);
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid updateType. Valid values: update, status")
                            .build();
            }
        } catch (Exception e) {
            log.error("Error in updateStore: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    private ResponseObject handleUpdateStore(StoreV3UpdateRequest request) {
        StoreRequest storeRequest = new StoreRequest();
        storeRequest.setName(request.getName());
        storeRequest.setAddress(request.getAddress());
        storeRequest.setStatus(request.getStatus());
        storeRequest.setDescription(request.getDescription());

        return ResponseObject.builder()
                .status(1000)
                .message("Store is updated")
                .data(storeService.updateStore(request.getId(), storeRequest))
                .build();
    }

    private ResponseObject handleUpdateStatus(StoreV3UpdateRequest request) {
        if (request.getStatusValue() == null || request.getStatusValue().trim().isEmpty()) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Status value is required for status update operation")
                    .build();
        }

        return ResponseObject.builder()
                .status(1000)
                .message("Store's status is updated")
                .data(storeService.updateStore(request.getId(), request.getStatusValue()))
                .build();
    }
}
