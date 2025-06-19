package com.scorelens.Controller.v1;


import com.scorelens.DTOs.Request.NotificationRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Notification", description = "Handling Notifications")
@RestController
@RequestMapping("v1/notifications")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationV1Controller {

    NotificationService notificationService;

    @PostMapping
    public ResponseObject newNotification(@RequestBody NotificationRequest request){
        return ResponseObject.builder()
                .status(1000)
                .message("New Notification")
                .data(notificationService.sendNotification(request))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject getNotificationListByBilliardMatchID(@PathVariable int id){
        return ResponseObject.builder()
                .status(1000)
                .message("Get Notification List")
                .data(notificationService.getNotificationsByMatch(id))
                .build();
    }

}
