package com.scorelens.DTOs.Response;


import com.scorelens.Enums.UserType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
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
