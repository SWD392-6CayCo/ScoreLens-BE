package com.scorelens.DTOs.Response;


import com.scorelens.Enums.UserType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationResponseDtoV2 {
    boolean authenticated;
    String accessToken;
    String refreshToken;
    Object user;
    UserType userType;
}
