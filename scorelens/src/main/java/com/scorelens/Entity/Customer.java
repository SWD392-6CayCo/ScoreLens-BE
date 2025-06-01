package com.scorelens.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;

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

    @Column(name = "status", length = 10)
    private String status;
}