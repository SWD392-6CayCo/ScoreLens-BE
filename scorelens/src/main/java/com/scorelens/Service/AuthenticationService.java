package com.scorelens.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.scorelens.DTOs.Request.AuthenticationRequestDto;
import com.scorelens.DTOs.Request.IntrospectRequestDto;
import com.scorelens.DTOs.Response.AuthenticationResponseDto;
import com.scorelens.DTOs.Response.CustomerResponseDto;
import com.scorelens.DTOs.Response.IntrospectResponseDto;
import com.scorelens.Entity.Customer;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.CustomerMapper;
import com.scorelens.Repository.CustomerRepo;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    CustomerRepo customerRepo;
    CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}") //Đọc từ file application.yaml
    protected String SIGNER_KEY;

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        var customer = customerRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), customer.getPassword());

        if(!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(customer);
        CustomerResponseDto responseDto = customerMapper.toDto(customer);
        return AuthenticationResponseDto.builder()
                .token(token)
                .authenticated(true)
                .user(responseDto)
                .build();
    }

    // tạo token
    private String generateToken(Customer c){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(c.getEmail())


                .claim("customerID", c.getCustomerID())
//                .claim("createAt", c.getCreateAt())
//                .claim("dob", c.getDob())
//                .claim("email", c.getEmail())
//                .claim("name", c.getName())
//                .claim("phoneNumber", c.getPhoneNumber())
//                .claim("status", c.getStatus())
//                .claim("type", c.getType())
//                .claim("updateAt", c.getUpdateAt())
                .issuer("scorelens")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public IntrospectResponseDto introspect(IntrospectRequestDto request)
            throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponseDto.builder()
                .valid(verified && expityTime.after(new Date()))
                .build();
    }
}
