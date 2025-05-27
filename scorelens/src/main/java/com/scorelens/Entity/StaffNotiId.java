package com.scorelens.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class StaffNotiId implements Serializable {
    private static final long serialVersionUID = -1950974435166485956L;
    @Column(name = "staffID", nullable = false, length = 50)
    private String staffID;

    @Column(name = "notiID", nullable = false, length = 50)
    private String notiID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StaffNotiId entity = (StaffNotiId) o;
        return Objects.equals(this.notiID, entity.notiID) &&
                Objects.equals(this.staffID, entity.staffID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notiID, staffID);
    }

}