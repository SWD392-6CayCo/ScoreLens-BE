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

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ResponseObject handleMessage(String message){
        return ResponseObject.builder().message(message).build();
    }

    @PostMapping("/send")
    public ResponseObject sendMessage(@RequestParam String message) {
        sendNotification(message);
        return ResponseObject.builder().message(message).build();
    }

    public void sendNotification(String message) {
        log.info("Sending notification: " + message);
        messagingTemplate.convertAndSend("/topic/public", message);
    }


}
