package com.scorelens.Entity;

import com.scorelens.Enums.StaffRole;
import com.scorelens.Enums.StatusType;
import com.scorelens.Enums.UserType;
import com.scorelens.Security.AppUser;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "Staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff implements AppUser {

    @Id
    @Column(name = "staffID", length = 10)
    private String staffID;  // Example: S01, M01, A01

    @ManyToOne
    @JoinColumn(name = "managerID")
    private Staff manager;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "phoneNumber", length = 10, nullable = false)
    private String phoneNumber;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "password", length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private StaffRole role; // STAFF, MANAGER, ADMIN

    @Column(name = "createAt")
    private LocalDate createAt;

    @Column(name = "updateAt")
    private LocalDate updateAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private StatusType status; // active, inactive

    @Override
    public String getId() {
        return staffID;
    }

    @Override
    public UserType getUserType() {
        return UserType.Staff;
    }
}
