package com.scorelens.Entity;

import com.scorelens.Enums.ResultStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teamID")
    private int teamID;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "totalScore")
    private int totalScore;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ResultStatus status; //-- win, lose, draw, pending

    @ManyToOne
    @JoinColumn(name = "billiardMatchID", nullable = false)
    private BilliardMatch billiardMatch;
}
