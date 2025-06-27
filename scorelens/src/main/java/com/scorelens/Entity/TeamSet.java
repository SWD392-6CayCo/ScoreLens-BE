package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TeamSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teamSetID")
    private int teamSetID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamID", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameSetID", nullable = false)
    private GameSet gameSet;

    @Column(name = "totalScore")
    private Integer totalScore;
}
