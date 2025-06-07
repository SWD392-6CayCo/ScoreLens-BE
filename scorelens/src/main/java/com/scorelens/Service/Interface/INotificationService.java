package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.NotificationRequest;
import com.scorelens.DTOs.Response.NotificationResponse;

public interface INotificationService {
    NotificationResponse sendNotification(NotificationRequest notificationRequest);
}
