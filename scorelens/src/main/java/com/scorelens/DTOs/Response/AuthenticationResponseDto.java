package com.scorelens.DTOs.Response;


import com.scorelens.Enums.UserType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationResponseDto {
    String token;
    boolean authenticated;
    Object user;
    UserType userType;
}
