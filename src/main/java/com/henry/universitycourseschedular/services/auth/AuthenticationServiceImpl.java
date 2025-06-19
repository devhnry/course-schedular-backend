package com.henry.universitycourseschedular.services.auth;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.enums.ContextType;
import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.enums.VerifyOtpResponse;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.AppUser;
import com.henry.universitycourseschedular.models.AuthToken;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models._dto.*;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.services.messaging.EmailService;
import com.henry.universitycourseschedular.services.messaging.OtpService;
import com.henry.universitycourseschedular.utils.OtpRateLimiter;
import com.henry.universitycourseschedular.utils.PasswordValidator;
import com.henry.universitycourseschedular.utils.UserContextService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import static com.henry.universitycourseschedular.services.auth.JwtService.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final UserContextService userContextService;
    private final AuthTokenRepository authTokenRepository;
    private final DepartmentRepository departmentRepository;
    private final CollegeBuildingRepository collegeBuildingRepository;
    private final PasswordValidator passwordValidator;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final OtpRateLimiter otpRateLimiter;
    private final EmailService emailService;
    @Value("${email.active}")
    private boolean isEmailActive;

    @Override
    public DefaultApiResponse<SuccessfulOnboardDto> signUp(OnboardRequestUserDto requestBody, String accountFor,
                                                           HttpServletResponse res) {
        if (appUserRepository.existsByEmailAddress(requestBody.emailAddress())) {
            return buildErrorResponse(String.format("%s already exists on the system.", accountFor));
        }

        if (requestBody.emailAddress() == null || requestBody.emailAddress().isBlank()) {
            return buildErrorResponse("Email cannot be empty.");
        }

        if (requestBody.emailAddress().contains("{{") || requestBody.emailAddress().contains("}}")) {
            return buildErrorResponse("Email contains invalid placeholder syntax.");
        }

        PasswordValidationResult result = PasswordValidator.validatePassword(requestBody.password());
        if (!result.isValid()) {
            return buildErrorResponse(result.getMessage());
        }

        if (!requestBody.password().equals(requestBody.confirmPassword())) {
            return buildErrorResponse("Passwords do not match.");
        }

        AppUser user = switch (accountFor) {
            case "DAPU" -> createNewDAPUUser(requestBody);
            case "HOD" -> createNewUser(requestBody);
            default -> new AppUser();
        };

        appUserRepository.save(user);
        TokenPair tokens = generateTokens(user);
        saveTokens(user, tokens.accessToken(), tokens.refreshToken());

        if(isEmailActive){
            otpRateLimiter.validateRateLimit(requestBody.emailAddress()); // Throw if over limit
        }
        String tokenExpiration = formatExpirationTime();
        SuccessfulOnboardDto data = new SuccessfulOnboardDto(
                user.getUserId(),
                user.getFullName(),
                user.getRole(),
                user.getEmailAddress(),
                tokens.accessToken,
                String.format("%s hrs",tokenExpiration),
                mapUserToDto(user)
        );

        setResponseCookie(res, tokens);

        if(isEmailActive){
            if(accountFor.equals("DAPU")){
                emailService.sendEmail(requestBody.emailAddress(),"Welcome to University Scheduler",new Context(), "WelcomeDapuOnboardTemplate");
            }else {
                emailService.sendEmail(requestBody.emailAddress(),"Welcome to University Scheduler",new Context(), "WelcomeOnboardTemplate");
            }
        }

        return buildSuccessResponse("Account created successfully.", StatusCodes.SIGNUP_SUCCESS, data);
    }

    @Override
    public DefaultApiResponse<UnverifiedLoginDto> login(LoginRequestDto requestBody) {
        try{
            AppUser user = appUserRepository.findByEmailAddress(requestBody.email())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Set<AuthToken> authTokens = authTokenRepository.findAllByUser_EmailAddress(requestBody.email());
            authTokens.forEach(authToken -> authToken.setExpiredOrRevoked(true));

            if (!passwordValidator.isPasswordCorrect(requestBody.password(), user.getPassword(), user.getEmailAddress())) {
                return buildErrorResponse("Invalid password", StatusCodes.INVALID_CREDENTIALS);
            }

            if(isEmailActive){
                otpRateLimiter.validateRateLimit(requestBody.email()); // Throw Exception if over limit
            }
            var otpResponse = otpService.sendOtp(requestBody.email(), ContextType.LOGIN);

            if (otpResponse.getStatusCode() == StatusCodes.GENERIC_FAILURE) {
                return buildErrorResponse("Unable to send OTP to email.");
            }

            UnverifiedLoginDto data = new UnverifiedLoginDto(
                    user.getEmailAddress(), false, otpResponse.getData()
            );

            return buildSuccessResponse("Account Found: Verify OTP to complete login", StatusCodes.ACTION_COMPLETED,  data);

        }catch (Exception e){
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<UnverifiedLoginDto> resendOtpForLogin(String email) {
        DefaultApiResponse<SuccessfulLoginDto> response = new DefaultApiResponse<>();

        try {
            AppUser user = appUserRepository.findByEmailAddress(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (isEmailActive) {
                otpRateLimiter.validateRateLimit(email); // prevent abuse
            }

            var otpResponse = otpService.sendOtp(email, ContextType.LOGIN);

            if (otpResponse.getStatusCode() == StatusCodes.GENERIC_FAILURE) {
                return buildErrorResponse("Unable to resend OTP to email.");
            }

            UnverifiedLoginDto data = new UnverifiedLoginDto(
                    user.getEmailAddress(), false, otpResponse.getData()
            );

            return buildSuccessResponse("OTP resent successfully.", StatusCodes.ACTION_COMPLETED, data);

        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<SuccessfulLoginDto> verifyLoginOtp(OneTimePasswordVerificationDto requestBody, HttpServletResponse response) {
        try {
            AppUser user = appUserRepository.findByEmailAddress(requestBody.email())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Optional<DefaultApiResponse<SuccessfulLoginDto>> otpCheckResult = verifyUserAndOtp(requestBody);
            if (otpCheckResult.isPresent()) {
                return otpCheckResult.get();
            }

            TokenPair tokens = generateTokens(user);
            saveTokens(user, tokens.accessToken(), tokens.refreshToken());

            setResponseCookie(response, tokens);

            return getSuccessfulLoginDtoDefaultApiResponse(user, tokens);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<OneTimePasswordDto> sendOtpForPasswordReset(String email) {
        try {
            boolean userExist = appUserRepository.existsByEmailAddress(email);
            if (userExist) {
                DefaultApiResponse<OneTimePasswordDto> otpResponse = otpService.sendOtp(email,
                        ContextType.FORGOT_PASSWORD);
                return buildSuccessResponse("OTP for Password Verification Sent.", StatusCodes.OTP_SENT, otpResponse.getData());
            }
            return buildErrorResponse("User does not exist. OTP Failed", StatusCodes.EMAIL_NOT_FOUND);
        }
        catch (RuntimeException e){
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<?> verifyPasswordResetOtp(OneTimePasswordVerificationDto requestBody) {
        AppUser user = appUserRepository.findByEmailAddress(requestBody.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<DefaultApiResponse<SuccessfulLoginDto>> otpCheckResult = verifyUserAndOtp(requestBody);

        if (otpCheckResult.isPresent()) {
            return otpCheckResult.get();
        }

        return buildSuccessResponse("OTP for Password Verified");
    }

    @Override
    public DefaultApiResponse<?> resetPassword(ResetPasswordDto requestBody) {
        try {
            AppUser user = appUserRepository.findByEmailAddress(requestBody.email())
                    .orElseThrow(() -> new ResourceNotFoundException("User Not found"));

            if(!requestBody.password().equals(requestBody.confirmPassword())){
                return buildErrorResponse("Password does not match confirm password.");
            }

            if(passwordValidator.matchesWithOldPassword(requestBody.password(), user.getPassword(), user.getEmailAddress())) {
                return buildErrorResponse("You have made use of this password!");
            }

            PasswordValidationResult result = PasswordValidator.validatePassword(requestBody.password());
            if (!result.isValid()) {
                return buildErrorResponse(result.getMessage());
            }

            user.setPassword(passwordEncoder.encode(requestBody.password()));
            appUserRepository.save(user);
            return buildSuccessResponse("Password Reset Successful: Prompt user to login");

        }catch (RuntimeException e){
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    @Transactional
    public DefaultApiResponse<String> refreshToken(String incomingRefreshToken, HttpServletResponse response) {

        String tokenId = getTokenId(incomingRefreshToken);
        log.info("Token passed into repo {}", tokenId);
        AuthToken maybeToken = authTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token Not Found"));

        if (Boolean.TRUE.equals(maybeToken.getExpiredOrRevoked())) {
            String email = maybeToken.getUser().getEmailAddress();
            authTokenRepository.findAllByUser_EmailAddress(email)
                    .forEach(t -> t.setExpiredOrRevoked(true));
            authTokenRepository.saveAll(
                    authTokenRepository.findAllByUser_EmailAddress(email)
            );
            return buildErrorResponse("Refresh token reuse detected. All sessions revoked.",
                    StatusCodes.UNAUTHORIZED_ACCESS);
        }

        if (jwtService.isTokenExpired(incomingRefreshToken)) {
            maybeToken.setExpiredOrRevoked(true);
            authTokenRepository.save(maybeToken);
            return buildErrorResponse("Refresh token expired", StatusCodes.UNAUTHORIZED_ACCESS);
        }

        maybeToken.setExpiredOrRevoked(true);
        authTokenRepository.save(maybeToken);

        AppUser user = maybeToken.getUser();
        TokenPair tokenPair = generateTokens(user);
        log.info("The token pair {}", tokenPair.refreshToken);
        saveTokens(user, tokenPair.accessToken, tokenPair.refreshToken);

        setResponseCookie(response, tokenPair);

        return buildSuccessResponse("Token refreshed", StatusCodes.ACTION_COMPLETED, tokenPair.accessToken);
    }

    @Override
    public DefaultApiResponse<?> logout(HttpServletRequest req, HttpServletResponse res) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or malformed Authorization header");
            return buildErrorResponse("Invalid logout request");
        }

        String token = authHeader.substring(7);
        AuthToken storedToken = authTokenRepository.findByAccessToken(token).orElse(null);
        if (storedToken == null) {
            log.warn("No token record found for: {}", token);
            return buildErrorResponse("Token not recognized");
        }

        storedToken.setExpiredOrRevoked(true);
        authTokenRepository.save(storedToken);
        log.info("Revoked token for user {}", storedToken.getUser().getEmailAddress());

        // Clear refresh token cookie
        ResponseCookie clearedCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, clearedCookie.toString());

        SecurityContextHolder.clearContext();
        return buildSuccessResponse("Logged out successfully.");
    }

    private AppUser createNewUser(OnboardRequestUserDto requestBody) {
        Department department = getDepartment(userContextService.getCurrentDepartmentCode());

        return AppUser.builder()
                .fullName(requestBody.fullName())
                .emailAddress(requestBody.emailAddress())
                .password(passwordEncoder.encode(requestBody.password()))
                .department(department)
                .collegeBuilding(department.getCollegeBuilding())
                .accountVerified(true)
                .writeAccess(false)
                .role(Role.HOD)
                .build();
    }

    private AppUser createNewDAPUUser(OnboardRequestUserDto requestBody) {
        return AppUser.builder()
                .fullName(requestBody.fullName())
                .emailAddress(requestBody.emailAddress())
                .password(passwordEncoder.encode(requestBody.password()))
                .accountVerified(true)
                .role(Role.DAPU)
                .writeAccess(true)
                .build();
    }

    private Department getDepartment(String code) {
        final String message = String.format("Department with ID %s not found.", code);
        return departmentRepository.findByCode(code).orElseThrow(
                () -> new RuntimeException(message)
        );
    }

    private AppUserDto mapUserToDto(AppUser user) {
        if (user.getRole().equals(Role.DAPU)) {
            return AppUserDto.builder()
                    .accountVerified(true)
                    .emailAddress(user.getEmailAddress())
                    .build();
        }
        return AppUserDto.builder()
                .accountVerified(true)
                .departmentCode(user.getDepartment().getCode())
                .emailAddress(user.getEmailAddress())
                .build();
    }

    private TokenPair generateTokens(AppUser user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmailAddress());
        claims.put("userId", user.getUserId());

        return new TokenPair(
                jwtService.createAccessToken(user),
                jwtService.generateRefreshToken(user, claims)
        );
    }

    private void saveTokens(AppUser user, String accessToken, String refreshToken) {

        SecretKey secretKey = jwtService.getSecretKey();
        if (secretKey == null) {
            throw new IllegalStateException("SecretKey is not initialized");
        }
        String jti = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload().getId();

        log.info("Token ID saved: {}", jti);
        log.info("Saving AuthTokens...");
        AuthToken token = AuthToken.builder()
                .tokenId(jti)
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredOrRevoked(false)
                .expiresAt(Instant.now().plusMillis(ACCESS_TOKEN_EXPIRATION_TIME))
                .build();
        authTokenRepository.save(token);
    }

    private <T> Optional<DefaultApiResponse<T>> verifyUserAndOtp(OneTimePasswordVerificationDto requestBody) {
        VerifyOtpResponse responseFromOtpService = otpService.verifyOtp(
                requestBody.oneTimePassword(),
                requestBody.email());

        String errorMessage = switch (responseFromOtpService){
            case NOT_FOUND -> "OTP not found";
            case USED -> "OTP used";
            case EXPIRED -> "OTP expired";
            case INVALID -> "OTP invalid";
            default -> null;
        };

        if (responseFromOtpService != VerifyOtpResponse.VERIFIED) {
            return Optional.of(buildErrorResponse(errorMessage));
        }
        return Optional.empty();
    }

    private void setResponseCookie(HttpServletResponse response, TokenPair tokens) {
        ResponseCookie refreshCookie = ResponseCookie.from("jid", tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth/refresh-token")
                .maxAge(Duration.ofDays(14))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String getTokenId(String refreshToken){
        SecretKey secretKey = jwtService.getSecretKey();
        if (secretKey == null) {
            throw new IllegalStateException("SecretKey is not initialized");
        }

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload().getId();
    }

    private String formatExpirationTime() {
        long seconds = JwtService.ACCESS_TOKEN_EXPIRATION_TIME / 1000;
        long minutes = seconds / 60;
        return minutes + " min";
    }

    private DefaultApiResponse<SuccessfulLoginDto> getSuccessfulLoginDtoDefaultApiResponse(AppUser user, TokenPair tokens) {
        SuccessfulLoginDto data = new SuccessfulLoginDto(
                user.getUserId(),
                user.getFullName(),
                user.getRole(),
                user.getEmailAddress(),
                true,
                tokens.accessToken(),
                tokens.refreshToken(),
                formatExpirationTime()
        );

        DefaultApiResponse<SuccessfulLoginDto> response = new DefaultApiResponse<>();
        response.setStatusCode(StatusCodes.OTP_SENT);
        response.setStatusMessage("OTP verified");
        response.setData(data);
        return response;
    }


    private record TokenPair(@NotNull String accessToken, @NotNull String refreshToken) {}

}