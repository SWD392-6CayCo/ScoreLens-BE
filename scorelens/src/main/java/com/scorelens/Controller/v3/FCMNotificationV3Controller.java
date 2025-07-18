package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.FCMNotificationV3Request;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.FCMService;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FCM Notification", description = "Manage FCM push notifications")
@RestController
@RequestMapping("/v3/fcm")
@CrossOrigin(origins = {"http://localhost:5173", "exp://192.168.90.68:8081", "https://scorelens.onrender.com"})
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FCMNotificationV3Controller {

    FCMService fcmService;

    /**
     * Unified FCM operation endpoint
     */
    @Operation(summary = "Unified FCM operations",
            description = "Unified API that combines send notification and register token operations")
    @PostMapping("/operation")
    public ResponseObject fcmOperation(@RequestBody FCMNotificationV3Request request) {
        try {
            String operationType = request.getOperationType();
            String tableID = request.getTableID();

            // Validate required fields
            if (operationType == null || operationType.trim().isEmpty()) {
                return ResponseObject.builder()
                        .status(400)
                        .message("Operation type is required")
                        .build();
            }

            if (tableID == null || tableID.trim().isEmpty()) {
                return ResponseObject.builder()
                        .status(400)
                        .message("Table ID is required")
                        .build();
            }

            Object data;
            String successMessage;

            switch (operationType.toLowerCase()) {
                case "send":
                    if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Title is required for send operation")
                                .build();
                    }
                    if (request.getBody() == null || request.getBody().trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Body is required for send operation")
                                .build();
                    }

                    try {
                        data = fcmService.sendNotification(tableID, request.getTitle(), request.getBody());
                        successMessage = "Notification sent successfully";
                        log.info("Sending FCM notification to token with tableID: {}, title: {}, body: {}",
                                tableID, request.getTitle(), request.getBody());
                    } catch (FirebaseMessagingException e) {
                        log.error("Error sending FCM notification: {}", e.getMessage(), e);
                        return ResponseObject.builder()
                                .status(1005)
                                .message("Failed to send notification")
                                .data(e.getMessage())
                                .build();
                    }
                    break;

                case "register":
                    if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("Token is required for register operation")
                                .build();
                    }

                    boolean success = fcmService.registerFcmToken(request.getToken(), tableID);
                    data = success;
                    if (success) {
                        successMessage = "Successfully registered FCM token";
                        log.info("FCM token registered successfully for tableID: {}", tableID);
                    } else {
                        return ResponseObject.builder()
                                .status(1005)
                                .message("Failed to register FCM token, please check your token or tableID again")
                                .data(success)
                                .build();
                    }
                    break;

                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid operation type. Supported types: send, register")
                            .build();
            }

            return ResponseObject.builder()
                    .status(1000)
                    .message(successMessage)
                    .data(data)
                    .build();

        } catch (Exception e) {
            log.error("Error in fcmOperation: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/{tableID}")
    public ResponseObject getFCMToken(@PathVariable String tableID) {
        String token = fcmService.getTargetToken(tableID);
        boolean isNull = !(token == null);
        if (!isNull) {
            return ResponseObject.builder()
                    .status(1005)
                    .message("FCM token with tableID: " + tableID + " not found")
                    .data(isNull)
                    .build();
        } else {
            return ResponseObject.builder()
                    .status(1000)
                    .message("FCM token: " + token)
                    .data(true)
                    .build();
        }
    }

}
