package com.scorelens.DTOs.Response;

import com.scorelens.Enums.UserType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoogleLoginResponseDto {
    boolean authenticated;
    Object user;
    UserType userType;
}
