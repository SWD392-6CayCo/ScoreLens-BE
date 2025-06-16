package com.scorelens.Service.Interface;

import com.scorelens.DTOs.Request.NotificationRequest;
import com.scorelens.DTOs.Response.NotificationResponse;

import java.util.List;

public interface INotificationService {
    NotificationResponse sendNotification(NotificationRequest notificationRequest);
    List<NotificationResponse> getNotificationsByMatch(int billiardMatchID);
    boolean deleteNotificationByMatchID(int billiardMatchID);
}
