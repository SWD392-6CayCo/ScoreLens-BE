package com.scorelens.Entity;

import com.scorelens.Enums.MatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class BilliardMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billiardMatchID")
    private int billiardMatchID;

    @ManyToOne
    @JoinColumn(name = "billardTableID", nullable = false)
    private BilliardTable billardTableID;

    @ManyToOne
    @JoinColumn(name = "modeID", nullable = false)
    private Mode modeID;

    @ManyToOne
    @JoinColumn(name = "byStaff")
    private Staff byStaff;

    @ManyToOne
    @JoinColumn(name = "byCustomer")
    private Customer byCustomer;

    @Column(name = "winner", length = 50)
    private String winner;

    @Column(name = "totalRound")
    private int totalRound;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status; //-- pending, ongoing, completed, cancelled, forfeited

    @Column(name = "code")
    private String code;

    @OneToMany(mappedBy = "billiardMatch", cascade = CascadeType.ALL)
    private List<GameSet> rounds = new ArrayList<>();

    @OneToMany(mappedBy = "billiardMatch", cascade = CascadeType.ALL)
    private List<Team> teams = new ArrayList<>();

}
