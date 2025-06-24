package com.scorelens.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Entity.Role;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.StaffRole;
import com.scorelens.Enums.StatusType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for {@link com.scorelens.Entity.Staff}
 */
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffResponseDto implements Serializable {
    String staffID;

    String name;
    String email;
    String phoneNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate dob;
    String address;
//    StaffRole role;
    Set<RoleResponse> roles;


    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate createAt;

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate updateAt;
    StatusType status;
    Staff manager;
    StoreResponse store;
}