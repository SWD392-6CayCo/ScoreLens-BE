package com.scorelens.Service;

import com.scorelens.DTOs.Request.NotificationRequest;
import com.scorelens.DTOs.Response.NotificationResponse;
import com.scorelens.Entity.Notification;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.NotificationMapper;
import com.scorelens.Repository.BilliardMatchRepository;
import com.scorelens.Repository.NotificationRepo;
import com.scorelens.Service.Interface.INotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationService implements INotificationService {

    NotificationMapper notificationMapper;

    NotificationRepo notificationRepo;

    BilliardMatchRepository billiardMatchRepo;

    @Override
    public NotificationResponse sendNotification(NotificationRequest notificationRequest) {
        if (!billiardMatchRepo.existsById(notificationRequest.getBilliardMatchID()))
            throw new AppException(ErrorCode.MATCH_NOT_FOUND);
        Notification noti = notificationMapper.toNotiRequest(notificationRequest);
        noti.setCreateAt(LocalDateTime.now());
        notificationRepo.save(noti);
        return notificationMapper.toNotiResponse(noti);
    }

    @Override
    public List<NotificationResponse> getNotificationsByMatch(int billiardMatchID) {
        List<Notification> list = notificationRepo.findAllByBilliardMatch_BilliardMatchID(billiardMatchID);
        if (list.isEmpty()) throw new AppException(ErrorCode.EMPTY_LIST);
        return notificationMapper.toNotiResponseList(list);
    }

    @Override
    public boolean deleteNotificationByMatchID(int billiardMatchID) {
        List<Notification> list = notificationRepo.findAllByBilliardMatch_BilliardMatchID(billiardMatchID);
        if (list.isEmpty()) throw new AppException(ErrorCode.EMPTY_LIST);
        notificationRepo.deleteAll(list);
        return true;
    }


}
