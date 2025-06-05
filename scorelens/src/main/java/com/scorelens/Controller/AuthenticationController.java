package com.scorelens.Controller;

import com.scorelens.DTOs.Request.AuthenticationRequestDto;
import com.scorelens.DTOs.Response.AuthenticationResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseObject authenticate(@RequestBody AuthenticationRequestDto request) {
        var result = authenticationService.authenticate(request);
        return ResponseObject.builder()
                .data(result)
                .message("Login successfully!!")
                .build();
    }

}
