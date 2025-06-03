package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CustomerNoti {
    @Id
    @Column(name = "customerID")
    private String customerID;

    @Id
    @Column(name = "notiID")
    private int notiID;

    @ManyToOne
    @JoinColumn(name = "customerID", insertable = false, updatable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "notiID", insertable = false, updatable = false)
    private Notification notification;
}
