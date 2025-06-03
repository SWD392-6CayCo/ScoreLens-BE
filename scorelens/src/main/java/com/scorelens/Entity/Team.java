package com.scorelens.Entity;

import com.scorelens.Enums.ResultStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teamID")
    private int teamID;

    @ManyToOne
    private BilliardMatch billiardMatch;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "totalScore")
    private int totalScore;

    @Column(name = "createAt")
    private LocalDate createAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ResultStatus status; //-- win, lose, draw, pending

}
