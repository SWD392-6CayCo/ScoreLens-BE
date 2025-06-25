package com.scorelens.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.scorelens.Entity.Customer}
 */
@AllArgsConstructor
@Data
public class CustomerResponseDto implements Serializable {
    private final String customerID;
    private final String name;
    private final String email;
    private final String phoneNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private final LocalDate dob;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private final LocalDate createAt;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private final LocalDate updateAt;
    private final String type;
    private final String status;
    private final String imageUrl;
}