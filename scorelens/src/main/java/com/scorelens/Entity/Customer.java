package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Customer {
    @Id
    @Column(name = "customerID", nullable = false, length = 50)
    private String customerID;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "createAt")
    private LocalDate createAt;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "type", length = 50)
    private String type;

//    @ManyToMany
//    @JoinTable(name = "CustomerNoti",
//            joinColumns = @JoinColumn(name = "customerID"),
//            inverseJoinColumns = @JoinColumn(name = "notiID"))
//    private Set<Notification> notifications = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "createByCustomer")
//    private Set<MatchPool> matchPools = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "customerID")
//    private Set<Player> players = new LinkedHashSet<>();

}