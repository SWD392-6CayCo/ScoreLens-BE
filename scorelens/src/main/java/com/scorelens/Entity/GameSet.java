package com.scorelens.Entity;

import com.scorelens.Enums.MatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class GameSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gameSetID")
    private int gameSetID;

    @Column(name = "gameSetNo")
    private int gameSetNo;

    @Column(name = "raceTo")
    private int raceTo;

    @Column(name = "winner")
    private String winner;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status;

    @ManyToOne
    @JoinColumn(name = "billiardMatchID", nullable = false)
    private BilliardMatch billiardMatch;

}
