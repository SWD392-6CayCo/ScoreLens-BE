package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.ShotEvent;
import com.scorelens.DTOs.Request.WebSocketV3Request;
import com.scorelens.DTOs.Request.WebsocketReq;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Enums.MessageType;
import com.scorelens.Enums.WSFCMCode;
import com.scorelens.Enums.WebSocketTopic;
import com.scorelens.Service.WebSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Web Socket", description = "Manage Noti")
@RestController
@RequestMapping("/v3/web_socket")
@CrossOrigin(origins = { "http://localhost:5173", "exp://192.168.90.68:8081", "https://scorelens.onrender.com",
        "http://localhost:8080" })
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketV3Controller {

    SimpMessagingTemplate messagingTemplate;

    WebSocketService webSocketService;


    // WebSocket receive → forward to /topic/notification
    @MessageMapping("/noti.send/{tableID}")
    public void handleNotification(String message, @DestinationVariable String tableID) {
        log.info("Received noti from table {}: {}", tableID, message);
        messagingTemplate.convertAndSend("/topic/notification/" + tableID, message);
    }

    // WebSocket receive → forward to /topic/logging_notification
    @MessageMapping("/log.send")
    public void handleLoggingNotification(String message, @DestinationVariable String tableID) {
        log.info("Received log from table {}: {}", tableID, message);
        messagingTemplate.convertAndSend("/topic/logging_notification/" + tableID, message);

    }

    // Unified REST API for all WebSocket message types
    @Operation(summary = "Send unified WebSocket message", description = "Unified API that combines notification, logging, and shot_event message types")
    @PostMapping()
    public ResponseObject sendMessage(@RequestBody WebSocketV3Request request) {

        WebsocketReq websocketReq;

        try {
            MessageType messageType = request.getMessageType();
            String tableID = request.getTableID();

            // Validate required fields
            if (messageType == null ) {
                return ResponseObject.builder()
                        .status(400)
                        .message("Message type is required")
                        .build();
            }

            if (tableID == null || tableID.trim().isEmpty()) {
                return ResponseObject.builder()
                        .status(400)
                        .message("Table ID is required")
                        .build();
            }

            Object data;
            String topic;
            String successMessage;

            switch (messageType) {
                case notification:
                    if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Message content is required for notification type")
                                .build();
                    }
                    topic = "/topic/notification/" + tableID;
                    websocketReq = new WebsocketReq(WSFCMCode.NOTIFICATION, request.getMessage().trim());
                    successMessage = "Notification sent successfully";
                    log.info("Sending notification from table {}: {}", tableID, request.getMessage());
                    break;

                case logging:
                    if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Message content is required for logging type")
                                .build();
                    }
                    topic = "/topic/logging_notification/" + tableID;
                    websocketReq = new WebsocketReq(WSFCMCode.LOGGING, request.getMessage());
                    successMessage = "Logging message sent successfully";
                    log.info("Sending logging from table {}: {}", tableID, request.getMessage());
                    break;

                case shot_event:
                    if (request.getShotEvent() == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Shot event data is required for shot_event type")
                                .build();
                    }
                    topic = "/topic/shot_event/" + tableID;
                    data = request.getShotEvent();
                    websocketReq = new WebsocketReq(WSFCMCode.SHOT, request.getShotEvent());
                    successMessage = "Shot event sent successfully";
                    log.info("Sending shot_event from table {}: {}", tableID, request.getShotEvent());
                    break;

                case set_event:
                    if (request.getShotEvent() == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Message data is required for set_event type")
                                .build();
                    }
                    topic = WebSocketTopic.NOTI_MOBILE + tableID;
                    websocketReq = new WebsocketReq((WSFCMCode.WINNING_SET), request.getMessage());
                    successMessage = "Set event sent successfully";
                    log.info("Sending set_event from table {}: {}", tableID, request.getShotEvent());
                    break;

                case match_event:
                    if (request.getShotEvent() == null) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Message data is required for set_event type")
                                .build();
                    }
                    topic = WebSocketTopic.NOTI_MOBILE + tableID;
                    websocketReq = new WebsocketReq((WSFCMCode.WINNING_MATCH), request.getMessage());
                    successMessage = "Match event sent successfully";
                    log.info("Sending match_event from table {}: {}", tableID, request.getShotEvent());
                    break;
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid message type. Supported types: notification, logging, shot_event")
                            .build();
            }

            // Send message to WebSocket topic

            messagingTemplate.convertAndSend(topic, websocketReq);

            return ResponseObject.builder()
                    .status(1000)
                    .message(successMessage)
                    .data(websocketReq)
                    .build();

        } catch (Exception e) {
            log.error("Error in sendMessage: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

}
