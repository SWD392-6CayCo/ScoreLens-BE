package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class MatchPool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matchID", nullable = false)
    private Integer id;

//    @Column(name = "type", length = 50)
//    private String type;
//
//    @Column(name = "raceTo")
//    private Integer raceTo;
//
//    @Column(name = "winner", length = 100)
//    private String winner;
//
//    @Column(name = "finalScore", length = 50)
//    private String finalScore;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "createByStaff")
//    private Staff createByStaff;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "createByCustomer")
//    private Customer createByCustomer;
//
//    @Column(name = "startTime")
//    private Instant startTime;
//
//    @Column(name = "endTime")
//    private Instant endTime;
//
//    @Column(name = "status", length = 50)
//    private String status;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "tableID")
//    private TablePool tableID;
//
//    @Column(name = "code", length = 50)
//    private String code;
//
//    @OneToMany(mappedBy = "matchID")
//    private Set<Event> events = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "matchID")
//    private Set<Notification> notifications = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "matchID")
//    private Set<Team> teams = new LinkedHashSet<>();

}