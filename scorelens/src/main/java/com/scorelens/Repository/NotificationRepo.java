package com.scorelens.Repository;

import com.scorelens.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notification, Integer> {

}
