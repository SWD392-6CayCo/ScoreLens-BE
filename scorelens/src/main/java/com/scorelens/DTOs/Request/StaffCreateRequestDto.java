package com.scorelens.DTOs.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Constants.RegexConstants;
import com.scorelens.Constants.ValidationMessages;
import com.scorelens.Enums.StaffRole;
import com.scorelens.Enums.StatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.scorelens.Entity.Staff}
 */
@AllArgsConstructor
@Data
@Builder
public class StaffCreateRequestDto implements Serializable {
//    private final String staffID;
//    private final Staff manager;

    private final String name;

    @Pattern(
            regexp = RegexConstants.VIETNAMESE_EMAIL,
            message = ValidationMessages.EMAIL_DOMAIN
    )
    private final String email;

    @Pattern(
            regexp = RegexConstants.VIETNAMESE_PHONE,
            message = ValidationMessages.PHONE_FORMAT
    )
    private final String phoneNumber;
    @Past(message = ValidationMessages.DOB_PAST)
    @Schema(type = "string", pattern = "dd-MM-yyyy")//Hiển thị format dd-MM-yyyy trên swagger
    @JsonFormat(pattern = "dd-MM-yyyy")
    private final LocalDate dob;

    private final String address;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @Size(min = 6, message = ValidationMessages.PASSWORD_LENGTH)
    private String password;

    private final StaffRole role;

    private final StatusType status;
}