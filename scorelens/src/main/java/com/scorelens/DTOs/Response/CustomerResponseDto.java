package com.scorelens.DTOs.Response;

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
    private final LocalDate dob;
    private final LocalDate createAt;
    private final LocalDate updateAt;
    private final String type;
    private final String status;
}