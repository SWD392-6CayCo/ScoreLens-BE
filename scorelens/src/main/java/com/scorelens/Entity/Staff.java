package com.scorelens.Entity;

import com.scorelens.Enums.StaffRole;
import com.scorelens.Enums.StatusType;
import com.scorelens.Enums.UserType;
import com.scorelens.Security.AppUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Staff implements AppUser {

    @Id
    @Column(name = "staffID", length = 10)
    String staffID;  // Example: S01, M01, A01

    @ManyToOne
    @JoinColumn(name = "managerID")
    Staff manager;

    @Column(name = "name", length = 100, nullable = false)
    String name;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    String email;

    @Column(name = "phoneNumber", length = 10, nullable = false)
    String phoneNumber;

    @Column(name = "dob")
    LocalDate dob;

    @Column(name = "address", length = 255)
    String address;

    @Column(name = "password", length = 100)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    StaffRole role; // STAFF, MANAGER, ADMIN

    @ManyToMany
    Set<Role> roles;

    @Column(name = "createAt")
    LocalDate createAt;

    @Column(name = "updateAt")
    LocalDate updateAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    StatusType status; // active, inactive

    @Override
    public String getId() {
        return staffID;
    }

    @Override
    public UserType getUserType() {
        return UserType.Staff;
    }
}
