    package com.scorelens.Controller.v1;

    import com.scorelens.Entity.ResponseObject;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.handler.annotation.SendTo;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;


    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Web Socket", description = "Manage Noti")
    @RestController
    @RequestMapping("/v1/noti")
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public class WebSocketV1Controller {

        SimpMessagingTemplate messagingTemplate;

        // WebSocket receive → forward to /topic/notification
        @MessageMapping("/noti.send")
        public void handleNotification(String message) {
            messagingTemplate.convertAndSend("/topic/notification", message);
        }

        // WebSocket receive → forward to /topic/logging_notification
        @MessageMapping("/log.send")
        public void handleLoggingNotification(String message) {
            messagingTemplate.convertAndSend("/topic/logging_notification", message);
        }

        // REST API gửi vào notification topic
        @PostMapping("/send-notification")
        public ResponseObject sendNotification(@RequestParam String message) {
            log.info("Sending notification: " + message);
            messagingTemplate.convertAndSend("/topic/notification", message);
            return ResponseObject.builder().message(message).build();
        }

        // REST API gửi vào logging_notification topic
        @PostMapping("/send-logging")
        public ResponseObject sendLogging(@RequestParam String message) {
            log.info("Sending logging message: " + message);
            messagingTemplate.convertAndSend("/topic/logging_notification", message);
            return ResponseObject.builder().message(message).build();
        }


    }
