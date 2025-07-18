package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.NotificationRequest;
import com.scorelens.DTOs.Response.NotificationResponse;
import com.scorelens.Entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toNotiRequest(NotificationRequest notificationRequest);
    NotificationResponse toNotiResponse(Notification notification);
    List<NotificationResponse> toNotiResponseList(List<Notification> notifications);

}
