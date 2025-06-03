package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gameID")
    private Integer gameID;

    @ManyToOne
    @JoinColumn(name = "billiardMatchID")
    private BilliardMatch billiardMatch;

    @Column(name = "raceTo")
    private Integer raceTo;

    @Column(name = "winner")
    private String winner;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @Column(name = "status")
    private String status;
}

