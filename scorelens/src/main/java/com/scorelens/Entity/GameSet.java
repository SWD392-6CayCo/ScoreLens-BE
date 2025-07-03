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

    @OneToMany(mappedBy = "gameSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    public void addEvent(Event event) {
        events.add(event);
        event.setGameSet(this);
    }

    @OneToMany(mappedBy = "gameSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamSet> tss = new ArrayList<>();

    public void addTeamSet(TeamSet teamSet) {
        tss.add(teamSet);
        teamSet.setGameSet(this);
    }
}
