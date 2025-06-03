package com.scorelens.Entity;

import com.scorelens.Enums.MatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BilliardMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billiardMatchID")
    private int billiardMatchID;

    @ManyToOne
    private BilliardTable billardTable;

    @ManyToOne
    private Mode mode;

    @ManyToOne
    private Staff byStaff;

    @Column(name = "raceTo")
    private int raceTo;

    @Column(name = "winner", length = 50)
    private String winner;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status; //-- pending, ongoing, completed, cancelled, forfeited
    private String code;
}
