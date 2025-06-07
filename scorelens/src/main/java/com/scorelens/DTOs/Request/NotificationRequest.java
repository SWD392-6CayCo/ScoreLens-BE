package com.scorelens.DTOs.Request;

import com.scorelens.Enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    private int roundID;
    private String message;
    private boolean isRead;
    private NotificationType type;
}
