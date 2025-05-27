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
public class CustomerNotiId implements Serializable {
    private static final long serialVersionUID = 472071735801596208L;
    @Column(name = "customerID", nullable = false, length = 50)
    private String customerID;

    @Column(name = "notiID", nullable = false, length = 50)
    private String notiID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CustomerNotiId entity = (CustomerNotiId) o;
        return Objects.equals(this.notiID, entity.notiID) &&
                Objects.equals(this.customerID, entity.customerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notiID, customerID);
    }

}