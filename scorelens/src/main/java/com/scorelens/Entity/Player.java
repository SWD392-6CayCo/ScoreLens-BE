package com.scorelens.Entity;

import com.scorelens.Enums.ResultStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playerID")
    private int playerID;

    @ManyToOne
    @JoinColumn(name = "teamID", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "customerID")
    private Customer customer;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "totalScore")
    private int totalScore;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ResultStatus status;
}
