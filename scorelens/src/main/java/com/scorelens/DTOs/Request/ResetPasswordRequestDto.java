package com.scorelens.DTOs.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequestDto {
    
    @NotBlank(message = "Reset token is required")
    String resetToken;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    String confirmPassword;
}
