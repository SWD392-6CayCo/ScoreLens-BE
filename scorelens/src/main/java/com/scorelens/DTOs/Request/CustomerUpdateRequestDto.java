package com.scorelens.DTOs.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scorelens.Constants.RegexConstants;
import com.scorelens.Constants.ValidationMessages;
import com.scorelens.Validation.DobConstraint;
import com.scorelens.Validation.PhoneConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.scorelens.Entity.Customer}
 */
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerUpdateRequestDto implements Serializable {
    String name;

    @Pattern(
            regexp = RegexConstants.VIETNAMESE_EMAIL,
            message = ValidationMessages.EMAIL_DOMAIN
    )
    String email;

    @PhoneConstraint(message = ValidationMessages.PHONE_FORMAT)
    String phoneNumber;

    @Past(message = ValidationMessages.DOB_PAST)
    @Schema(type = "string", pattern = "dd-MM-yyyy")//Hiển thị format dd-MM-yyyy trên swagger
    @JsonFormat(pattern = "dd-MM-yyyy")
    @DobConstraint(min = 10)
    LocalDate dob;

    String managerID;
    String imageUrl;
}