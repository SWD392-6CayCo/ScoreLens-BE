package com.scorelens.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
@Table(name="customer")
public class Customer {
    @Id
    @Column(name = "customerID", nullable = false, length = 50)
    private String customerID;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phoneNumber", length = 100)
    private String phoneNumber;

    @Column(name = "password", length = 100)
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dob", nullable = true)
    private Date dob;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "createAt", nullable = true)
    private Date createAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "updateAt", nullable = true)
    private Date updateAt;

    @Column(name = "type", length = 10)
    private String type;

    @Column(name = "status", length = 10)
    private String status;
}