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
public class Staff {
    @Id
    @Column(name = "staffID", nullable = false, length = 50)
    private String staffID;

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

    @Column(name = "managerID", length = 50)
    private String managerID;

    @Column(name = "createAt")
    private LocalDate createAt;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "phoneNumber", length = 20)
    private String phoneNumber;

    @Column(name = "updateAt")
    private LocalDate updateAt;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "createByStaff")
    private Set<MatchPool> matchPools = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "createByStaff")
    private Set<Notification> notifications = new LinkedHashSet<>();

}