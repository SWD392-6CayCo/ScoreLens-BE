package com.scorelens.Controller.v3;

import com.scorelens.Config.KafKaHeartBeat;
import com.scorelens.DTOs.Request.HeartBeatV3Request;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.KafkaService.KafkaProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "HeartBeat V3", description = "Unified HeartBeat API")
@RestController
@RequestMapping("v3/heartbeats")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HeartBeatV3Controller {
    
    KafkaProducer kafkaProducer;
    KafKaHeartBeat kafkaHeartBeat;

    @Operation(summary = "Perform heartbeat operations with unified parameters", 
               description = "Unified API that combines all POST operations from v1 controller")
    @PostMapping
    public ResponseObject performHeartBeatOperation(@RequestBody HeartBeatV3Request request) {
        try {
            String operationType = request.getOperationType();
            if (operationType == null) {
                return ResponseObject.builder()
                        .status(400)
                        .message("operationType is required. Valid values: send, start, stop")
                        .build();
            }
            
            switch (operationType.toLowerCase()) {
                case "send":
                    return handleSendHeartbeat(request);
                case "start":
                    return handleStartHeartbeat();
                case "stop":
                    return handleStopHeartbeat();
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid operationType. Valid values: send, start, stop")
                            .build();
            }
        } catch (Exception e) {
            log.error("Error in performHeartBeatOperation: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }
    
    private ResponseObject handleSendHeartbeat(HeartBeatV3Request request) {
        if (request.getTableId() == null || request.getTableId().trim().isEmpty()) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Table ID is required for send heartbeat operation")
                    .build();
        }
        
        kafkaProducer.sendHeartbeat(request.getTableId());
        return ResponseObject.builder()
                .status(1000)
                .message("Heartbeat sent manually.")
                .build();
    }
    
    private ResponseObject handleStartHeartbeat() {
        kafkaHeartBeat.start();
        return ResponseObject.builder()
                .status(1000)
                .message("Heartbeat started.")
                .build();
    }
    
    private ResponseObject handleStopHeartbeat() {
        kafkaHeartBeat.stop();
        return ResponseObject.builder()
                .status(1000)
                .message("Heartbeat stopped.")
                .build();
    }
    
    @GetMapping("/status")
    public ResponseObject getStatus() {
        String statusMessage = "Heartbeat is " + (kafkaHeartBeat.isRunning() ? "running" : "stopped");
        return ResponseObject.builder()
                .status(1000)
                .message(statusMessage)
                .data(kafkaHeartBeat.isRunning())
                .build();
    }
}
