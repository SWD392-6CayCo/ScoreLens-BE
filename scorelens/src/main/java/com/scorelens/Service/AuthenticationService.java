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
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.UserType;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.CustomerMapper;
import com.scorelens.Mapper.StaffMapper;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Security.AppUser;
import com.scorelens.Service.Interface.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements IAuthenticationService {

    AppUserService appUserService;
    CustomerMapper customerMapper;
    StaffMapper staffMapper;
    CustomerRepo customerRepo;
    StaffRepository staffRepo;
    private final PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}") //Đọc từ file application.yaml
    protected String SIGNER_KEY;

    //--------------------------------------- AUTHENTICATION --------------------------------------------------
    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        AppUser appUser = appUserService.authenticateUser(request.getEmail(), request.getPassword());
        Object responseUser;
        String token;
        switch (appUser.getUserType()) {
            case Customer -> {
                Customer customer = customerRepo.findById(appUser.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
                responseUser = customerMapper.toDto(customer);
            }
            case Staff -> {
                Staff staff = staffRepo.findById(appUser.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
                responseUser = staffMapper.toDto(staff);
            }
            default -> throw new AppException(ErrorCode.UNSUPPORTED_USER_TYPE);
        }
        token = generateToken(appUser);
        return AuthenticationResponseDto.builder()
                .authenticated(true)
                .token(token)
                .user(responseUser)
                .userType(appUser.getUserType())
                .build();
    }

    // tạo token
    private String generateToken(Object userEntity) {
        String email;
        String userId;
        String scope;

        if (userEntity instanceof Customer customer) {
            email = customer.getEmail();
            userId = customer.getCustomerID();
            scope = buildScope(customer);
        } else if (userEntity instanceof Staff staff) {
            email = staff.getEmail();
            userId = staff.getStaffID();
            scope = buildScope(staff);
        } else {
            throw new AppException(ErrorCode.UNSUPPORTED_USER_TYPE);
        }

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(email) // name của biến authentication
                .claim("userID", userId)
                .claim("scope", scope)
                .issuer("scorelens")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(1,
                        ChronoUnit.HOURS)))
                .build();

        try {
            JWSObject jwsObject = new JWSObject(
                    new JWSHeader(JWSAlgorithm.HS512),
                    new Payload(claims.toJSONObject())
            );
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Token generation failed", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(Object userEntity) {
        if (userEntity instanceof Customer) {
            return UserType.Customer.name(); // "Customer"
        } else if (userEntity instanceof Staff staff) {
            //return staff.getRole().name();   // "Staff" / "Manager" / "Admin"
            StringJoiner stringJoiner = new StringJoiner(" ");
            if(!CollectionUtils.isEmpty(staff.getRoles()))
                staff.getRoles().forEach(role -> {
                    stringJoiner.add("ROLE_" + role.getName());
                    if (!CollectionUtils.isEmpty(role.getPermissions()))
                        role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
                });



            return stringJoiner.toString();   // "Staff" / "Manager" / "Admin"
        } else {
            throw new AppException(ErrorCode.UNSUPPORTED_USER_TYPE);
        }
    }

    @Override
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
