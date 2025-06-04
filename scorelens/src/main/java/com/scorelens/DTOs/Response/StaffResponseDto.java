package com.scorelens.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.StaffRole;
import com.scorelens.Enums.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.scorelens.Entity.Staff}
 */
@AllArgsConstructor
@Getter
public class StaffResponseDto implements Serializable {
    private final String staffID;
    private final Staff manager;
    private final String name;
    private final String email;
    private final String phoneNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private final LocalDate dob;
    private final String address;
    private final StaffRole role;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private final LocalDate createAt;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private final LocalDate updateAt;
    private final StatusType status;
}