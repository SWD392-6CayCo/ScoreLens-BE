package com.scorelens.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Data
@Table(name="customer")
public class Customer {
    @Id
    @Column(name = "customerid", nullable = false, length = 50)
    private String customerID;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "role", length = 50)
    private String role;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dob", nullable = true)
    private Date dob;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "create_at", nullable = true)
    private Date createAt;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "type", length = 50)
    private String type;


}