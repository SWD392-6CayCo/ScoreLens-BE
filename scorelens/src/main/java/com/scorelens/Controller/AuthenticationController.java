package com.scorelens.Controller;

import com.nimbusds.jose.JOSEException;
import com.scorelens.DTOs.Request.AuthenticationRequestDto;
import com.scorelens.DTOs.Request.CustomerCreateRequestDto;
import com.scorelens.DTOs.Request.IntrospectRequestDto;
import com.scorelens.DTOs.Response.AuthenticationResponseDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.AuthenticationService;
import com.scorelens.Service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Tag(name = "Authentication", description = "Authentication APIs")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;
    CustomerService customerService;

    @PostMapping("/login")
    ResponseObject authenticate(@RequestBody AuthenticationRequestDto request) {
        var result = authenticationService.authenticate(request);
        return ResponseObject.builder()
                .status(1000)
                .data(result)
                .message("Login successfully!!")
                .build();
    }

    @PostMapping("/register")
    ResponseObject register(@RequestBody @Valid CustomerCreateRequestDto request) {
        CustomerResponseDto response = customerService.createCustomer(request);
        return ResponseObject.builder()
                .status(1000)
                .data(response)
                .message("Register successfully")
                .build();
    }

    @PostMapping("/introspect")
    ResponseObject authenticate(@RequestBody IntrospectRequestDto request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ResponseObject.builder()
                .status(1000)
                .data(result)
                .build();
    }

}
