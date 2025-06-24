package com.scorelens.DTOs.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Constants.RegexConstants;
import com.scorelens.Constants.ValidationMessages;
import com.scorelens.Validation.DobConstraint;
import com.scorelens.Validation.PhoneConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.scorelens.Entity.Customer}
 */
@AllArgsConstructor
@Data
@Builder
public class CustomerCreateRequestDto implements Serializable {

    @Size(min = 2, message = "NAME_LENGTH")
    private final String name;

    @Pattern(
            regexp = RegexConstants.VIETNAMESE_EMAIL,
            message = ValidationMessages.EMAIL_DOMAIN
    )
    private final String email;
    
    @PhoneConstraint(message = ValidationMessages.PHONE_FORMAT)
    private final String phoneNumber;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @Size(min = 6, message = "PASSWORD_LENGTH")
    private String password;

    @Past(message = ValidationMessages.DOB_PAST)
    @Schema(type = "string", pattern = "dd-MM-yyyy")//Hiển thị format dd-MM-yyyy trên swagger
    @JsonFormat(pattern = "dd-MM-yyyy")
    @DobConstraint(min = 12, message = "INVALID_DOB") // 10 tuổi mới cho bắn bida :))
    private final LocalDate dob;
}