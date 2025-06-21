package com.scorelens.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.AuthenticationResponseDto;
import com.scorelens.DTOs.Response.AuthenticationResponseDtoV2;
import com.scorelens.DTOs.Response.IntrospectResponseDto;
import com.scorelens.Entity.Customer;
import com.scorelens.Entity.InvalidatedToken;
import com.scorelens.Entity.Staff;
import com.scorelens.Enums.UserType;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.CustomerMapper;
import com.scorelens.Mapper.StaffMapper;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.InvalidatedTokenRepository;
import com.scorelens.Repository.StaffRepository;
import com.scorelens.Security.AppUser;
import com.scorelens.Service.Interface.IAuthenticationService;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationV2Service implements IAuthenticationService {

    AppUserService appUserService;
    CustomerMapper customerMapper;
    StaffMapper staffMapper;
    CustomerRepo customerRepo;
    StaffRepository staffRepo;
    InvalidatedTokenRepository invalidatedTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}") //Đọc từ file application.yaml
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}") //Đọc từ file application.yaml
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}") //Đọc từ file application.yaml
    protected long REFRESHABLE_DURATION;

    //--------------------------------------- AUTHENTICATION --------------------------------------------------
    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        AppUser appUser = appUserService.authenticateUser(request.getEmail(), request.getPassword());
        Object responseUser;
        String token;
        switch (appUser.getUserType()) {
            case CUSTOMER -> {
                Customer customer = customerRepo.findById(appUser.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
                responseUser = customerMapper.toDto(customer);
            }
            case STAFF -> {
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

    public record AuthTokens(AuthenticationResponseDtoV2 responseDto, String accessToken, String refreshToken) {}
    public AuthTokens authenticateV2(AuthenticationRequestDto request) {
        AppUser appUser = appUserService.authenticateUser(request.getEmail(), request.getPassword());
        Object responseUser;

        switch (appUser.getUserType()) {
            case CUSTOMER -> {
                Customer c = customerRepo.findById(appUser.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
                responseUser = customerMapper.toDto(c);
            }
            case STAFF -> {
                Staff s = staffRepo.findById(appUser.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
                responseUser = staffMapper.toDto(s);
            }
            default -> throw new AppException(ErrorCode.UNSUPPORTED_USER_TYPE);
        }
        String accessToken = generateTokenV2(appUser, VALID_DURATION);
        String refreshToken = generateTokenV2(appUser, REFRESHABLE_DURATION);

         AuthenticationResponseDtoV2 responseDto = AuthenticationResponseDtoV2.builder()
                .authenticated(true)
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
                .user(responseUser)
                .userType(appUser.getUserType())
                .build();
         return new AuthTokens(responseDto, accessToken, refreshToken);
    }

    //--------------------------------------- LOGOUT -----------------------------------------------------------
    public void logout(LogoutRequestDto request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jti = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e){
            log.info("Token is already expired");
        }
    }

    //-----------------------------------VERIFY---------------------------------------
    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                :
                signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if(!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

//    public AuthTokens refreshTokenV2(RefreshV2Request request) throws ParseException, JOSEException {
//        SignedJWT signedJWT = verifyToken(request.getRefreshToken(), true);
//
//        String jti = signedJWT.getJWTClaimsSet().getJWTID();
//        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//
//        //Blacklist refresh token cũ
//        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
//                .id(jti)
//                .expiryTime(expiryTime)
//                .build();
//        invalidatedTokenRepository.save(invalidatedToken);
//
//        String email = signedJWT.getJWTClaimsSet().getSubject();
//        AppUser user = staffRepo.findByEmail(email)
//                .map(s -> (AppUser) s)
//                .orElseGet(() -> customerRepo.findByEmail(email)
//                        .map(c -> (AppUser) c)
//                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST)));
//
//        String newAccessToken = generateTokenV2(user, VALID_DURATION);
//        String newRefreshToken = generateTokenV2(user, REFRESHABLE_DURATION);
//
//        AuthenticationResponseDtoV2 responseDto = AuthenticationResponseDtoV2.builder()
//                .authenticated(true)
////                .accessToken(newAccessToken)
////                .refreshToken(newRefreshToken)
//                .build();
//
//        return new AuthTokens(responseDto, newAccessToken, newRefreshToken);
//    }

    public AuthTokens refreshTokenV2(String refreshToken) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(refreshToken, true);

        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Blacklist token cũ
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        String email = signedJWT.getJWTClaimsSet().getSubject();
        AppUser user = staffRepo.findByEmail(email)
                .map(s -> (AppUser) s)
                .orElseGet(() -> customerRepo.findByEmail(email)
                        .map(c -> (AppUser) c)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST)));

        String newAccessToken = generateTokenV2(user, VALID_DURATION);
        String newRefreshToken = generateTokenV2(user, REFRESHABLE_DURATION);

        AuthenticationResponseDtoV2 responseDto = AuthenticationResponseDtoV2.builder()
                .authenticated(true)
                .build();

        return new AuthTokens(responseDto, newAccessToken, newRefreshToken);
    }


    public AuthenticationResponseDto refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getAccessToken(), true);

        var jti = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        //lưu token đã bị invalidated vào db - phần này của logout
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var email = signJWT.getJWTClaimsSet().getSubject();
        AppUser user = staffRepo.findByEmail(email)
                .map(s -> (AppUser) s)
                .orElseGet(() -> customerRepo.findByEmail(email)
                        .map(c -> (AppUser) c)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST)));

        String token = generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String generateTokenV2(Object o, long duration){
        String email;
        String userId;
        String scope;

        if(o instanceof Customer c){
            email = c.getEmail();
            userId = c.getId();
            scope = buildScope(c);
        } else if(o instanceof Staff s){
            email = s.getEmail();
            userId = s.getId();
            scope = buildScope(s);
        } else {
            throw new AppException(ErrorCode.UNSUPPORTED_USER_TYPE);
        }

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(email)
                .claim("userID", userId)
                .claim("scope", scope)
                .jwtID(UUID.randomUUID().toString())
                .issuer("scorelens")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(duration)))
                .build();

        try {
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS512),
                    claims
            );
            signedJWT.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Token generation failed", e);
            throw new AppException(ErrorCode.UNSUPPORTED_USER_TYPE);
        }

    }

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
                .subject(email)
                .claim("userID", userId)
                .claim("scope", scope)
                .jwtID(UUID.randomUUID().toString())
                .issuer("scorelens")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .build();

        try {
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS512),
                    claims
            );
            signedJWT.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return signedJWT.serialize(); // SignedJWT trả về chuỗi JWT chuẩn
        } catch (JOSEException e) {
            log.error("Token generation failed", e);
            throw new RuntimeException(e);
        }
    }


    //build ra scope - chứa role và các permission
    private String buildScope(Object userEntity) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (userEntity instanceof Customer) {
            return stringJoiner.add("ROLE_" + UserType.CUSTOMER.name()).toString(); // "Customer"
        } else if (userEntity instanceof Staff staff) {
            //return staff.getRole().name();   // "Staff" / "Manager" / "Admin"
            if(!CollectionUtils.isEmpty(staff.getRoles()))
                staff.getRoles().forEach(role -> {
                    stringJoiner.add("ROLE_" + role.getName());
                    if (!CollectionUtils.isEmpty(role.getPermissions()))
                        role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
                });

            return stringJoiner.toString();   // "STAFF" / "MANAGER" / "ADMIN"
        } else {
            throw new AppException(ErrorCode.UNSUPPORTED_USER_TYPE);
        }
    }

    @Override
    public IntrospectResponseDto introspect(IntrospectRequestDto request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;;
        try {
            verifyToken(token, false);
        } catch (AppException e){
            isValid = false;
        }
        return IntrospectResponseDto.builder()
                .valid(isValid)
                .build();
    }
}
