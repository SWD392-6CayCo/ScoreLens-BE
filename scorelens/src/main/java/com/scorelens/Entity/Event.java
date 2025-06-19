package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventID")
    private int eventID;

    @ManyToOne
    @JoinColumn(name = "playerID")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "gameSetID")
    private GameSet gameSet;

    @Column(name = "scoreValue")
    private boolean scoreValue;

    @Column(name = "isFoul")
    private boolean isFoul;

    @Column(name = "isUncertain")
    private boolean isUncertain;

    @Column(name = "timeStamp")
    private LocalDateTime timeStamp;

    @Lob //Kiểu text trong mysql
    @Column(name = "message")
    private String message;

    @Column(name = "sceneUrl")
    private String sceneUrl;
}
