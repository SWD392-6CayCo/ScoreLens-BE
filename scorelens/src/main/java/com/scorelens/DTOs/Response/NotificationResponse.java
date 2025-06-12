package com.scorelens.DTOs.Response;

import com.scorelens.Enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {
    private int notificationID;
    private int roundID;
    private String message;
    private boolean isRead;
    private NotificationType type;
    private LocalDateTime createAt;
}
