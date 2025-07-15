package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.ModeRequest;
import com.scorelens.DTOs.Request.ModeV3Request;
import com.scorelens.DTOs.Response.ModeResponse;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.ModeService;
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
@Tag(name = "Mode V3", description = "Unified Mode API")
@RestController
@RequestMapping("v3/modes")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeV3Controller {
    
    @Autowired
    ModeService modeService;

    @Operation(summary = "Get modes with unified parameters", 
               description = "Unified API that combines all GET operations from v1 controller")
    @GetMapping
    public ResponseObject getModes(
            @Parameter(description = "Query type: all, byId")
            @RequestParam(required = false, defaultValue = "all") String queryType,
            
            @Parameter(description = "Mode ID (required for queryType=byId)")
            @RequestParam(required = false) Integer modeId,
            
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
                    data = modeService.getAll();
                    message = "Get Mode list";
                    break;
                    
                case "byid":
                    if (modeId == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Mode ID is required for queryType=byId")
                                .build();
                    }
                    data = modeService.getById(modeId);
                    message = "Get Mode information successfully";
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
            log.error("Error in getModes: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping
    public ResponseObject createMode(@RequestBody ModeRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Create new Mode successfully")
                .data(modeService.createMode(request))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseObject updateMode(@PathVariable Integer id, @RequestBody ModeRequest request) {
        return ResponseObject.builder()
                .status(1000)
                .message("Update Mode information successfully")
                .data(modeService.updateMode(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseObject delete(@PathVariable Integer id) {
        return ResponseObject.builder()
                .status(1000)
                .message("Mode with ID " + id + " has been deleted")
                .data(modeService.delete(id))
                .build();
    }
}
