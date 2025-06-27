package com.scorelens.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Enums.StatusType;
import com.scorelens.Enums.UserType;
import com.scorelens.Security.AppUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name="customer")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer implements AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customerID", nullable = false, length = 50)
    String customerID;

    @Column(name = "name", length = 100)
    String name;

    @Column(name = "email", length = 100)
    String email;

    @Column(name = "phoneNumber", length = 100)
    String phoneNumber;

    @Column(name = "password", length = 100)
    String password;

    @Schema(type = "string", pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "dob", nullable = true)
    LocalDate dob;

    @Schema(type = "string", pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "createAt", nullable = true)
    LocalDate createAt;

    @Schema(type = "string", pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "updateAt", nullable = true)
    LocalDate updateAt;

    @Column(name = "type", length = 10)
    String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    StatusType status; //active, inactive

    @Column(name = "imageUrl", length = 500)
    String imageUrl; // URL của ảnh trên S3

    @Override
    public String getId() {
        return customerID;
    }

    @Override
    public UserType getUserType() {
        return UserType.CUSTOMER;
    }
}