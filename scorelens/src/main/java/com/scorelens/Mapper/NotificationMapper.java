package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.NotificationRequest;
import com.scorelens.DTOs.Response.NotificationResponse;
import com.scorelens.Entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "billiardMatchID", target = "billiardMatch.billiardMatchID")
    Notification toNotiRequest(NotificationRequest notificationRequest);

    @Mapping(source = "billiardMatch.billiardMatchID", target = "billiardMatchID")
    NotificationResponse toNotiResponse(Notification notification);

    @Mapping(source = "billiardMatch.billiardMatchID", target = "billiardMatchID")
    List<NotificationResponse> toNotiResponseList(List<Notification> notifications);

}
