package com.scorelens.DTOs.Request;

import com.scorelens.Enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NotificationRequest {
    private int billiardMatchID;
    private String message;
    private NotificationType type;
}
