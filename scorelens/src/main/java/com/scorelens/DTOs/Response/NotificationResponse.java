package com.scorelens.DTOs.Response;

import com.scorelens.Enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {
    private int notificationID;
    private int billiardMatchID;
    private String message;
    private NotificationType type;
    private LocalDateTime createAt;
}
