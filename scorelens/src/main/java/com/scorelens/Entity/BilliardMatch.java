package com.scorelens.Entity;

import com.scorelens.Enums.MatchStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
    private Integer billiardMatchID;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "billardTableID", nullable = false)
    private BilliardTable billardTable;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "modeID", nullable = false)
    private Mode mode;

    @ManyToOne
    @JoinColumn(name = "staffID")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "customerID")
    private Customer customer;

    @Column(name = "setUp")
    private String setUp;

    @Column(name = "winner", length = 50)
    private String winner;

    @Column(name = "totalSet")
    private Integer totalSet;

    @NotNull
    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status; //-- pending, ongoing, completed, cancelled, forfeited

    @NotNull
    @Column(name = "code")
    private String code;

    @OneToMany(mappedBy = "billiardMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameSet> sets = new ArrayList<>();

    public void addSet(GameSet set) {
        sets.add(set);
        set.setBilliardMatch(this);
    }

    @OneToMany(mappedBy = "billiardMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    public void addTeam(Team team) {
        teams.add(team);
        team.setBilliardMatch(this);
    }

    @OneToMany(mappedBy = "billiardMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notis = new ArrayList<>();

    public void addNotification(Notification notification) {
        notis.add(notification);
        notification.setBilliardMatch(this);
    }
}
