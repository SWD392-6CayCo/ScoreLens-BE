package com.scorelens.Controller.v3;

import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.FCMService;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
     * Send notification via Firebase Cloud Messaging
     */
    @PostMapping("/send")
    public ResponseObject sendNotification(@RequestParam String tableID, @RequestParam String title, @RequestParam String body) {
        try {
            log.info("Sending FCM notification to token with tableID: {}, title: {}, body: {}", tableID, title, body);
            String response = fcmService.sendNotification(tableID, title, body);
            return ResponseObject.builder()
                    .status(1000)
                    .message("Notification sent successfully")
                    .data(response)
                    .build();
        } catch (FirebaseMessagingException e) {
            log.error("Error sending FCM notification: {}", e.getMessage(), e);
            return ResponseObject.builder()
                    .status(9999)
                    .message("Failed to send notification")
                    .data(e.getMessage())
                    .build();
        }
    }


    @PostMapping("/register")
    public ResponseObject registerFCMToken(@RequestParam String token, @RequestParam String tableID) {
        boolean success = fcmService.registerFcmToken(token, tableID);
        if (success) {
            return ResponseObject.builder()
                    .status(1000)
                    .message("Successfully registered FCM token")
                    .data(success)
                    .build();
        } else {
            return ResponseObject.builder()
                    .status(1005)
                    .message("Failed to register FCM token, please check your token or tableID again")
                    .data(success)
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
