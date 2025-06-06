package com.scorelens.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Enums.StatusType;
import com.scorelens.Enums.UserType;
import com.scorelens.Security.AppUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name="customer")
public class Customer implements AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Schema(type = "string", pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "dob", nullable = true)
    private LocalDate dob;

    @Schema(type = "string", pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "createAt", nullable = true)
    private LocalDate createAt;

    @Schema(type = "string", pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "updateAt", nullable = true)
    private LocalDate updateAt;

    @Column(name = "type", length = 10)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private StatusType status; //active, inactive

    @Override
    public String getId() {
        return customerID;
    }

    @Override
    public UserType getUserType() {
        return UserType.CUSTOMER;
    }
}