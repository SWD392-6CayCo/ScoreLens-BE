package com.scorelens.Repository;

import com.scorelens.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByBilliardMatch_BilliardMatchID(int billiardMatchID);

}
