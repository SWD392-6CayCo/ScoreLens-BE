package com.scorelens.Entity;

import jakarta.persistence.*;

@Entity
public class StaffNoti {
    @Id
    @Column(name = "staffID")
    private String staffID;

    @Id
    @Column(name = "notiID")
    private int notiID;

    @ManyToOne
    @JoinColumn(name = "staffID", insertable = false, updatable = false)
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "notiID", insertable = false, updatable = false)
    private Notification notification;
}

