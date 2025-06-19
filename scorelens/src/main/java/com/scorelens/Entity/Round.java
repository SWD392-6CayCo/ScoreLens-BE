package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roundID")
    private int roundID;

    @Column(name = "roundNo")
    private int roundNo;

    @Column(name = "raceTo")
    private String raceTo;

    @Column(name = "winner")
    private String winner;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "billiardMatchID", nullable = false)
    private BilliardMatch billiardMatch;

}
