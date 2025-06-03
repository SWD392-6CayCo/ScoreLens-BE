package com.scorelens.Entity;

import com.scorelens.Enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationID")
    private int notificationID;

    @ManyToOne
    @JoinColumn(name = "billiardMatchID")
    private BilliardMatch billiardMatch;

    @Column(name = "message")
    private String message;

    @Column(name = "isRead")
    private boolean isRead;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10)
    private NotificationType type; //start, end, special
}

