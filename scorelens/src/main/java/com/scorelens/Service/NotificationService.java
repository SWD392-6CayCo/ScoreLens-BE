package com.scorelens.Service;

import com.scorelens.DTOs.Request.NotificationRequest;
import com.scorelens.DTOs.Response.NotificationResponse;
import com.scorelens.Entity.Notification;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.NotificationMapper;
import com.scorelens.Repository.BilliardMatchRepo;
import com.scorelens.Repository.NotificationRepo;
import com.scorelens.Service.Interface.INotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationService implements INotificationService {

    NotificationMapper notificationMapper;

    NotificationRepo notificationRepo;

    BilliardMatchRepo billiardMatchRepo;

    @Override
    public NotificationResponse sendNotification(NotificationRequest notificationRequest) {
        if (!billiardMatchRepo.existsById(notificationRequest.getBilliardMatchID()))
            throw new AppException(ErrorCode.MATCH_NOT_FOUND);
        Notification noti = notificationMapper.toNotiRequest(notificationRequest);
        notificationRepo.save(noti);
        return notificationMapper.toNotiResponse(noti);
    }
}
