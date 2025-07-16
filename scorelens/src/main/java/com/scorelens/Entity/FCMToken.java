package com.scorelens.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class FCMToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcmTokenID", nullable = false)
    private int fcmTokenID;

    @ManyToOne
    @JoinColumn(name = "billardTableID", nullable = false)
    private BilliardTable billardTable;

    @Column(name = "targetToken", length = 255, nullable = false)
    private String targetToken;


}
