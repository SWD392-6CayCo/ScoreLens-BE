package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchID")
    private MatchPool matchID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerID")
    private Player playerID;

    @Column(name = "scoreValue")
    private Boolean scoreValue;

    @Column(name = "timeStamp")
    private Instant timeStamp;

    @Column(name = "imageURL")
    private String imageURL;

    @Column(name = "capturedByAI", length = 50)
    private String capturedByAI;

    @Column(name = "isFoul")
    private Boolean isFoul;

    @Column(name = "message")
    private String message;

}