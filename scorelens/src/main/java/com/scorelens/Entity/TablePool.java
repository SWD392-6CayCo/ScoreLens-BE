package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class TablePool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tableID", nullable = false)
    private Integer id;

//    @Column(name = "type", length = 50)
//    private String type;
//
//    @Column(name = "name", length = 100)
//    private String name;
//
//    @Column(name = "status", length = 50)
//    private String status;
//
//    @Column(name = "isActive")
//    private Boolean isActive;
//
//    @OneToMany(mappedBy = "tableID")
//    private Set<MatchPool> matchPools = new LinkedHashSet<>();

}