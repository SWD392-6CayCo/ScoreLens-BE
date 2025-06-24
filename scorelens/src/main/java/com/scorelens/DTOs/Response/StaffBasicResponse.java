package com.scorelens.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Enums.StatusType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for basic Staff information (used for manager field to avoid circular reference)
 */
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffBasicResponse implements Serializable {
    String staffID;
    String name;
    String email;
    String phoneNumber;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate dob;
    
    String address;
    StatusType status;
}
