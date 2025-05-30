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
public class Notification {
    @Id
    @Column(name = "notiID", nullable = false, length = 50)
    private String notiID;

//    @Lob
//    @Column(name = "message")
//    private String message;
//
//    @Column(name = "isRead")
//    private Boolean isRead;
//
//    @Column(name = "type", length = 50)
//    private String type;
//
//    @Column(name = "createAt")
//    private Instant createAt;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "matchID")
//    private MatchPool matchID;
//
//    @Column(name = "customerID", length = 50)
//    private String customerID;
//
//    @ManyToMany(mappedBy = "notifications")
//    private Set<Customer> customers = new LinkedHashSet<>();
//
//    @ManyToMany(mappedBy = "notifications")
//    private Set<Staff> staff = new LinkedHashSet<>();

}