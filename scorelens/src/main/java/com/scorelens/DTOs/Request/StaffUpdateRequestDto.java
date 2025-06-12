package com.scorelens.DTOs.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Constants.RegexConstants;
import com.scorelens.Constants.ValidationMessages;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.StatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.scorelens.Entity.Staff}
 */
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffUpdateRequestDto implements Serializable {
    String name;

    @Pattern(
            regexp = RegexConstants.VIETNAMESE_EMAIL,
            message = ValidationMessages.EMAIL_DOMAIN
    )
    String email;

    @Pattern(
            regexp = RegexConstants.VIETNAMESE_PHONE,
            message = ValidationMessages.PHONE_FORMAT
    )
    String phoneNumber;

    @Past(message = ValidationMessages.DOB_PAST)
    @Schema(type = "string", pattern = "dd-MM-yyyy")//Hiển thị format dd-MM-yyyy trên swagger
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate dob;

    String address;
    StatusType status;

    List<String> roles;

    String managerID;
}