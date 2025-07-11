    package com.scorelens.Controller.v1;

    import com.scorelens.DTOs.Request.ShotEvent;
    import com.scorelens.Entity.ResponseObject;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import lombok.extern.slf4j.Slf4j;
    import org.checkerframework.checker.units.qual.A;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.handler.annotation.SendTo;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.web.bind.annotation.*;


    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Web Socket", description = "Manage Noti")
    @RestController
    @RequestMapping("/v1/noti")
    @CrossOrigin(origins = {"http://localhost:5173", "exp://192.168.90.68:8081", "https://scorelens.onrender.com"})
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public class WebSocketV1Controller {

        SimpMessagingTemplate messagingTemplate;

        // WebSocket receive → forward to /topic/notification
        @MessageMapping("/noti.send")
        public void handleNotification(String message) {
            messagingTemplate.convertAndSend("/topic/notification/23374e21-2391-41b0-b275-651df88b3b04", message);
            System.out.println("Received noti: " + message);
        }

        // WebSocket receive → forward to /topic/logging_notification
        @MessageMapping("/log.send")
        public void handleLoggingNotification(String message) {
            messagingTemplate.convertAndSend("/topic/logging_notification/23374e21-2391-41b0-b275-651df88b3b04", message);
        }

        // REST API forward to notification topic
        @PostMapping("/send-notification")
        public ResponseObject sendNotification(@RequestParam String message) {
            log.info("Sending notification: " + message);
            messagingTemplate.convertAndSend("/topic/notification/23374e21-2391-41b0-b275-651df88b3b04", message);
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(message)
                    .build();
        }

        // REST API forward to logging_notification topic
        @PostMapping("/send-logging")
        public ResponseObject sendLogging(@RequestParam String message) {
            log.info("Sending logging message: " + message);
            messagingTemplate.convertAndSend("/topic/logging_notification/23374e21-2391-41b0-b275-651df88b3b04", message);
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(message)
                    .build();
        }

        // REST API forward to shot_event topic
        @PostMapping("/shot_event")
        public ResponseObject shotEvent(@RequestBody ShotEvent event) {
            log.info("Sending shot event: " + event);
            messagingTemplate.convertAndSend("/topic/shot_event/23374e21-2391-41b0-b275-651df88b3b04", event);
            return ResponseObject.builder()
                    .status(1000)
                    .message("shot event")
                    .data(event)
                    .build();
        }


    }
