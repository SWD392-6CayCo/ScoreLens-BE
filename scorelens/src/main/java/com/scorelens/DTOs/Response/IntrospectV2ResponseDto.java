package com.scorelens.DTOs.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectV2ResponseDto {
    boolean isAuth;
    String userID;
    String username;
    String role;
}
