package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CustomerNoti {
    @EmbeddedId
    private CustomerNotiId id;

    @MapsId("customerID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customerID", nullable = false)
    private Customer customerID;

    @MapsId("notiID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notiID", nullable = false)
    private Notification notiID;

}