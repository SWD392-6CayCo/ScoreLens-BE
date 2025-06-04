package com.scorelens.DTOs.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangePasswordRequestDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 6)
    private String newPassword;
}
