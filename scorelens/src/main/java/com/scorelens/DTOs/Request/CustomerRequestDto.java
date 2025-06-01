package com.scorelens.DTOs.Request;

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
public class CustomerRequestDto implements Serializable {
    private final String name;
    @Email(message = "Email must be a valid Vietnamese domain (e.g., @gmail.com, @yahoo.com.vn)", regexp = "^[a-zA-Z0-9._%+-]+@(gmail\\\\.com|yahoo\\\\.com\\\\.vn|outlook\\\\.com\\\\.vn|hocmai\\\\.vn|fpt\\\\.edu\\\\.vn|vnpt\\\\.vn)$")
    private final String email;
    @Pattern(message = "Phone number must start with 0 and contain 8 or 10 digits", regexp = "^0\\\\d{7}(\\\\d{2})?$")
    private final String phoneNumber;
    @NotNull(message = "Password can not be null")
    @Size(message = "Password at least 6 characters", min = 6)
    @NotEmpty(message = "Password can not be empty")
    @NotBlank(message = "Password can not be left blank")
    private final String password;
    @FutureOrPresent(message = "Thời gian sinh chỉ được phép ở trong quá khứ")
    private final LocalDate dob;
}