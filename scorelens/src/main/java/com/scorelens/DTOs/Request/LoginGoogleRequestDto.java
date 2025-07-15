package com.scorelens.DTOs.Request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginGoogleRequestDto {
    String email;
    String name;
    String picture;
}
