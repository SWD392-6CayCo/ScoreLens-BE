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
import com.scorelens.DTOs.Response.IntrospectV2ResponseDto;
import com.scorelens.Service.Email.EmailService;
import jakarta.mail.MessagingException;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

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
    PasswordEncoder passwordEncoder;
    EmailService emailService;
    RedisTokenService redisTokenService;
    RedisTemplate<String, Object> redisTemplate;

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
    public void logout(String accessToken, String refreshToken) throws ParseException, JOSEException {
        blacklistToken(accessToken);
        blacklistToken(refreshToken);
    }

    private void blacklistToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(token, true); // Dùng `true` để không check thời gian quá chặt
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Sử dụng Redis để blacklist token (nhanh hơn database)
        redisTokenService.invalidateToken(jti, expiryTime);

        // Backup: Vẫn lưu vào database để đảm bảo (optional)
        if (!invalidatedTokenRepository.existsById(jti)) {
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
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

        String jti = signedJWT.getJWTClaimsSet().getJWTID();

        // Check Redis first (faster than database)
        if(redisTokenService.isTokenInvalidated(jti)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Fallback: Check database if Redis is down
        if(invalidatedTokenRepository.existsById(jti)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

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
        String username;

        if(o instanceof Customer c){
            email = c.getEmail();
            username = c.getName();
            userId = c.getId();
            scope = buildScope(c);
        } else if(o instanceof Staff s){
            email = s.getEmail();
            username = s.getName();
            userId = s.getId();
            scope = buildScope(s);
        } else {
            throw new AppException(ErrorCode.UNSUPPORTED_USER_TYPE);
        }

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(email)
                .claim("userID", userId)
                .claim("username", username)
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

    public IntrospectV2ResponseDto introspectV2(String accessToken) {
        try {
            // Check if token exists
            if (accessToken == null || accessToken.isEmpty()) {
                return IntrospectV2ResponseDto.builder()
                        .isAuth(false)
                        .userID(null)
                        .username(null)
                        .role(null)
                        .userType(null)
                        .build();
            }

            // Use existing introspect logic to validate token
            IntrospectRequestDto request = IntrospectRequestDto.builder()
                    .token(accessToken)
                    .build();

            var introspectResult = introspect(request);

            if (!introspectResult.isValid()) {
                return IntrospectV2ResponseDto.builder()
                        .isAuth(false)
                        .userID(null)
                        .username(null)
                        .role(null)
                        .userType(null)
                        .build();
            }

            // Token is valid, extract user info
            SignedJWT signedJWT = verifyToken(accessToken, false);
//            String email = signedJWT.getJWTClaimsSet().getSubject();
            String username = signedJWT.getJWTClaimsSet().getStringClaim("username");
            String userID = signedJWT.getJWTClaimsSet().getStringClaim("userID");
            String scope = signedJWT.getJWTClaimsSet().getStringClaim("scope");

            // Extract role from scope (e.g., "ROLE_ADMIN DELETE_CUSTOMER..." -> "ADMIN")
            String role = extractRoleFromScope(scope);

            // Determine userType based on userID or find user in database
            UserType userType = determineUserType(userID);

            return IntrospectV2ResponseDto.builder()
                    .isAuth(true)
                    .userID(userID)
                    .username(username)
                    .role(role)
                    .userType(userType)
                    .build();

        } catch (Exception e) {
            return IntrospectV2ResponseDto.builder()
                    .isAuth(false)
                    .userID(null)
                    .username(null)
                    .role(null)
                    .userType(null)
                    .build();
        }
    }

    /**
     * Giải mã role từ scope string
     * Input: "ROLE_ADMIN DELETE_CUSTOMER GET_STAFF_LIST UPDATE_STAFF_DETAIL..."
     * Output: "ADMIN"
     */
    private String extractRoleFromScope(String scope) {
        if (scope == null || scope.isEmpty()) {
            return null;
        }

        // Split scope by spaces and find the ROLE_ prefix
        String[] scopeParts = scope.split(" ");
        for (String part : scopeParts) {
            if (part.startsWith("ROLE_")) {
                // Remove "ROLE_" prefix and return the role
                return part.substring(5); // "ROLE_ADMIN" -> "ADMIN"
            }
        }

        return null;
    }

     //Kiểm tra userType dựa trên userId
     // kiểm tra nếu userId tồn tại trên table Customer hoặc Staff
    private UserType determineUserType(String userID) {
        if (userID == null || userID.isEmpty()) {
            return null;
        }

        // Check if user exists in Customer table
        if (customerRepo.findById(userID).isPresent()) {
            return UserType.CUSTOMER;
        }

        // Check if user exists in Staff table
        if (staffRepo.findById(userID).isPresent()) {
            return UserType.STAFF;
        }

        return null;
    }

    //--------------------------------------- FORGOT PASSWORD --------------------------------------------------

    //Gửi mail reset password
    public void forgotPassword(ForgotPasswordRequestDto request) {
        String email = request.getEmail();
        log.info("Processing forgot password request for email: {}", email);

        // Tìm user theo email (Customer hoặc Staff)
        Customer customer = customerRepo.findByEmail(email).orElse(null);
        Staff staff = staffRepo.findByEmail(email).orElse(null);

        if (customer == null && staff == null) {
            log.warn("No user found with email: {}", email);
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        // Tạo reset token và lưu vào Redis (thay thế JWT)
        String resetToken = generateSimpleResetToken();
        String userName = customer != null ? customer.getName() : staff.getName();

        try {
            redisTokenService.saveResetToken(email, resetToken);
            log.info("Reset token saved to Redis for email: {}", email);
        } catch (Exception e) {
            log.error("Failed to save reset token to Redis for email: {}", email, e);
            throw new AppException(ErrorCode.REDIS_CONNECTION_FAILED);
        }

        try {
            // Gửi email
            emailService.sendForgotPasswordEmail(email, resetToken, userName);
            log.info("Forgot password email sent successfully to: {}", email);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send forgot password email to: {}", email, e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    //reset password với token
    public void resetPassword(ResetPasswordRequestDto request) {
        String resetToken = request.getResetToken();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        log.info("Processing reset password request");

        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Verify reset token từ Redis (one-time use)
        String email = verifyResetTokenFromRedis(resetToken);
        if(email == null){
            throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
        }

        // Tìm user và update password
        Customer customer = customerRepo.findByEmail(email).orElse(null);
        Staff staff = staffRepo.findByEmail(email).orElse(null);

        if (customer != null) {
            // Update customer password
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerRepo.save(customer);
            log.info("Password reset successfully for customer: {}", email);

            // Gửi email xác nhận
            try {
                emailService.sendPasswordResetSuccessEmail(email, customer.getName());
            } catch (MessagingException | java.io.UnsupportedEncodingException e) {
                log.warn("Failed to send password reset success email to: {}", email, e);
            }

        } else if (staff != null) {
            // Update staff password
            staff.setPassword(passwordEncoder.encode(newPassword));
            staffRepo.save(staff);
            log.info("Password reset successfully for staff: {}", email);

            // Gửi email xác nhận
            try {
                emailService.sendPasswordResetSuccessEmail(email, staff.getName());
            } catch (MessagingException | java.io.UnsupportedEncodingException e) {
                log.warn("Failed to send password reset success email to: {}", email, e);
            }

        } else {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
    }

    /**
     * Tạo simple reset token (thay thế JWT)
     */
    private String generateSimpleResetToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Verify reset token từ Redis và trả về email (one-time use)
     */
    private String verifyResetTokenFromRedis(String resetToken) {
        try {
            // Tìm email theo token trong Redis (và xóa token sau khi dùng)
            Set<String> keys = redisTemplate.keys("reset_token:*");
            if (keys != null) {
                for (String key : keys) {
                    String storedToken = (String) redisTemplate.opsForValue().get(key);
                    if (resetToken.equals(storedToken)) {
                        // Extract email from key
                        String email = key.replace("reset_token:", "");
                        // Delete token after use (one-time use)
                        redisTemplate.delete(key);
                        log.info("Reset token verified and deleted for email: {}", email);
                        return email;
                    }
                }
            }
            log.warn("Reset token not found or expired: {}", resetToken);
            return null;
        } catch (Exception e) {
            log.error("Error verifying reset token from Redis", e);
            return null;
        }
    }
}
