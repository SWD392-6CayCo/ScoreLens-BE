package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.ShotEvent;
import com.scorelens.DTOs.Request.WebSocketV3Request;
import com.scorelens.Entity.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "WebSocket V3", description = "Unified WebSocket API")
@RestController
@RequestMapping("v3/websockets")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketV3Controller {
    
    SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Send WebSocket messages with unified parameters", 
               description = "Unified API that combines all POST operations from v1 controller")
    @PostMapping("/send")
    public ResponseObject sendWebSocketMessage(@RequestBody WebSocketV3Request request) {
        try {
            String messageType = request.getMessageType();
            if (messageType == null) {
                return ResponseObject.builder()
                        .status(400)
                        .message("messageType is required. Valid values: notification, logging, shotEvent")
                        .build();
            }
            
            switch (messageType.toLowerCase()) {
                case "notification":
                    return handleSendNotification(request);
                case "logging":
                    return handleSendLogging(request);
                case "shotevent":
                    return handleSendShotEvent(request);
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid messageType. Valid values: notification, logging, shotEvent")
                            .build();
            }
        } catch (Exception e) {
            log.error("Error in sendWebSocketMessage: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }
    
    private ResponseObject handleSendNotification(WebSocketV3Request request) {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Message is required for notification operation")
                    .build();
        }
        
        log.info("Sending notification: " + request.getMessage());
        messagingTemplate.convertAndSend("/topic/notification/23374e21-2391-41b0-b275-651df88b3b04", request.getMessage());
        return ResponseObject.builder()
                .status(1000)
                .message(request.getMessage())
                .data(request.getMessage())
                .build();
    }
    
    private ResponseObject handleSendLogging(WebSocketV3Request request) {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseObject.builder()
                    .status(400)
                    .message("Message is required for logging operation")
                    .build();
        }
        
        log.info("Sending logging message: " + request.getMessage());
        messagingTemplate.convertAndSend("/topic/logging_notification/23374e21-2391-41b0-b275-651df88b3b04", request.getMessage());
        return ResponseObject.builder()
                .status(1000)
                .message(request.getMessage())
                .data(request.getMessage())
                .build();
    }
    
    private ResponseObject handleSendShotEvent(WebSocketV3Request request) {
        if (request.getShotEvent() == null) {
            return ResponseObject.builder()
                    .status(400)
                    .message("ShotEvent is required for shot event operation")
                    .build();
        }
        
        log.info("Sending shot event: " + request.getShotEvent());
        messagingTemplate.convertAndSend("/topic/shot_event/23374e21-2391-41b0-b275-651df88b3b04", request.getShotEvent());
        return ResponseObject.builder()
                .status(1000)
                .message("shot event")
                .data(request.getShotEvent())
                .build();
    }
}
