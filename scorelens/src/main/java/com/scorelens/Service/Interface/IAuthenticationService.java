package com.scorelens.Service.Interface;

import com.nimbusds.jose.JOSEException;
import com.scorelens.DTOs.Request.AuthenticationRequestDto;
import com.scorelens.DTOs.Request.IntrospectRequestDto;
import com.scorelens.DTOs.Response.AuthenticationResponseDto;
import com.scorelens.DTOs.Response.IntrospectResponseDto;

import java.text.ParseException;

public interface IAuthenticationService {
    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);
    IntrospectResponseDto introspect(IntrospectRequestDto request)
            throws JOSEException, ParseException;
}
