package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StaffNoti {
    @EmbeddedId
    private StaffNotiId id;

    @MapsId("staffID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staffID", nullable = false)
    private Staff staffID;

    @MapsId("notiID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notiID", nullable = false)
    private Notification notiID;

}