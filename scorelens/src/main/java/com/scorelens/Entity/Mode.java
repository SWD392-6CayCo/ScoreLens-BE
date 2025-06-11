package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Mode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int modeID;

    @Column(name = "name", length = 50)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "isActive") // 0: available, 1: unavailable
    private boolean isActive;
}


