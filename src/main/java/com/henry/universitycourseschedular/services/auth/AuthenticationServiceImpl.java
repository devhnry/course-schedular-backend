package com.henry.universitycourseschedular.services.auth;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.enums.ContextType;
import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.enums.VerifyOtpResponse;
import com.henry.universitycourseschedular.models._dto.*;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.user.AppUser;
import com.henry.universitycourseschedular.models.user.AuthToken;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.services.messaging.EmailService;
import com.henry.universitycourseschedular.services.messaging.OtpService;
import com.henry.universitycourseschedular.utils.OtpRateLimiter;
import com.henry.universitycourseschedular.utils.PasswordValidator;
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
import java.util.Arrays;
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
    private final AuthTokenRepository authTokenRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordValidator passwordValidator;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final OtpRateLimiter otpRateLimiter;
    private final EmailService emailService;
    @Value("${email.active}")
    private boolean isEmailActive;

    private static DefaultApiResponse<SuccessfulLoginDto> getSuccessfulLoginDtoDefaultApiResponse(AppUser user, TokenPair tokens) {
        SuccessfulLoginDto data = new SuccessfulLoginDto();
        data.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        data.setUserId(user.getUserId());
        data.setRole(user.getRole());
        data.setEmail(user.getEmailAddress());
        data.setAccessToken(tokens.accessToken());
        data.setTokenExpirationDuration("24hrs");
        data.setLoginVerified(true);
        data.setRole(user.getRole());


        DefaultApiResponse<SuccessfulLoginDto> response = new DefaultApiResponse<>();
        response.setStatusCode(StatusCodes.OTP_SENT);
        response.setStatusMessage("OTP verified");
        response.setData(data);
        return response;
    }

    @Override
    public DefaultApiResponse<SuccessfulOnboardDto> signUp(OnboardUserDto requestBody, String accountFor,
                                                           HttpServletResponse res) {
        DefaultApiResponse<SuccessfulOnboardDto> response;

        if (appUserRepository.existsByEmailAddress(requestBody.emailAddress())) {
            return buildErrorResponse(String.format("%s already exists on the system.", accountFor));
        }

        PasswordValidationResult result = PasswordValidator.validatePassword(requestBody.password());
        if (!result.isValid()) {
            return buildErrorResponse(result.getMessage());
        }

        if (!requestBody.password().equals(requestBody.confirmPassword())) {
            return buildErrorResponse("Passwords do not match.");
        }

        AppUser user = switch (accountFor) {
            case "DAPU" -> createNewUser(requestBody, Role.DAPU);
            case "HOD" -> createNewUser(requestBody);
            default -> new AppUser();
        };

        appUserRepository.save(user);
        TokenPair tokens = generateTokens(user);
        saveTokens(user, tokens.accessToken(), tokens.refreshToken());

        if(isEmailActive){
            otpRateLimiter.validateRateLimit(requestBody.emailAddress()); // Throw if over limit
        }
        String tokenExpiration = String.valueOf(ACCESS_TOKEN_EXPIRATION_TIME / 3600);
        SuccessfulOnboardDto data = new SuccessfulOnboardDto(
                user.getUserId(),
                String.format("%s %s", user.getFirstName(), user.getLastName()),
                user.getRole(),
                user.getEmailAddress(),
                tokens.accessToken,
                String.format("%s hrs",tokenExpiration),
                mapUserToDto(user)
        );

        setResponseCookie(res, tokens);
        response = buildSuccessResponse("Account created successfully.", StatusCodes.SIGNUP_SUCCESS, data);

        if(isEmailActive){
            if(accountFor.equals("DAPU")){
                emailService.sendEmail(requestBody.emailAddress(),"Welcome to University Scheduler",new Context(), "WelcomeDapuOnboardTemplate");
            }else {
                emailService.sendEmail(requestBody.emailAddress(),"Welcome to University Scheduler",new Context(), "WelcomeOnboardTemplate");
            }
        }

        return response;
    }

    @Override
    public DefaultApiResponse<SuccessfulLoginDto> login(LoginUserDto requestBody) {
        DefaultApiResponse<SuccessfulLoginDto> response = new DefaultApiResponse<>();

        try{
            AppUser user = appUserRepository.findByEmailAddress(requestBody.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Set<AuthToken> authTokens = authTokenRepository.findAllByUser_EmailAddress(requestBody.getEmail());
            authTokens.forEach(authToken -> authToken.setExpiredOrRevoked(true));

            System.out.print(Arrays.toString(authTokens.toArray()));

            if (!passwordValidator.isPasswordCorrect(requestBody.getPassword(), user.getPassword(), user.getEmailAddress())) {
                return buildErrorResponse("Invalid password");
            }

            if(isEmailActive){
                otpRateLimiter.validateRateLimit(requestBody.getEmail()); // Throw if over limit
            }
            var otpResponse = otpService.sendOtp(requestBody.getEmail(), ContextType.LOGIN);

            if (otpResponse.getStatusCode() == StatusCodes.GENERIC_FAILURE) {
                return buildErrorResponse("Unable to send OTP to email.");
            }

            SuccessfulLoginDto data = new SuccessfulLoginDto();
            data.setEmail(user.getEmailAddress());
            data.setLoginVerified(false);
            data.setOneTimePassword(otpResponse.getData());

            response.setStatusCode(StatusCodes.ACTION_COMPLETED);
            response.setStatusMessage("Account Found: Verify OTP to complete login");
            response.setData(data);
            return response;

        }catch (RuntimeException e){
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<SuccessfulLoginDto> verifyLoginOtp(VerifyOtpDto requestBody, HttpServletResponse response) {
        try {
            AppUser user = appUserRepository.findByEmailAddress(requestBody.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<DefaultApiResponse<SuccessfulLoginDto>> otpCheckResult = verifyUserAndOtp(requestBody);
            if (otpCheckResult.isPresent()) {
                return otpCheckResult.get();
            }

            TokenPair tokens = generateTokens(user);
            saveTokens(user, tokens.accessToken(), tokens.refreshToken());

            setResponseCookie(response, tokens);

            return getSuccessfulLoginDtoDefaultApiResponse(user, tokens);
        } catch (RuntimeException e) {
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
            return buildErrorResponse("User does not exist. OTP Failed");
        }
        catch (RuntimeException e){
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<?> verifyPasswordResetOtp(VerifyOtpDto requestBody) {
        AppUser user = appUserRepository.findByEmailAddress(requestBody.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<DefaultApiResponse<SuccessfulLoginDto>> otpCheckResult = verifyUserAndOtp(requestBody);

        if (otpCheckResult.isPresent()) {
            return otpCheckResult.get();
        }

        return buildSuccessResponse("OTP for Password Verified");
    }

    @Override
    public DefaultApiResponse<?> resetPassword(ResetPasswordDto requestBody) {
        try {
            AppUser user = appUserRepository.findByEmailAddress(requestBody.getEmail())
                    .orElseThrow(() -> new RuntimeException("User Not found"));

            if(!requestBody.getPassword().equals(requestBody.getConfirmPassword())){
                return buildErrorResponse("Password does not match confirm password.");
            }

            if(passwordValidator.matchesWithOldPassword(requestBody.getPassword(), user.getPassword(), user.getEmailAddress())) {
                return buildErrorResponse("You have made use of this password!");
            }

            PasswordValidationResult result = PasswordValidator.validatePassword(requestBody.getPassword());
            if (!result.isValid()) {
                return buildErrorResponse(result.getMessage());
            }

            user.setPassword(passwordEncoder.encode(requestBody.getPassword()));
            appUserRepository.save(user);
            return buildSuccessResponse("Password Reset Successful: Prompt user to login");

        }catch (RuntimeException e){
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    @Transactional
    public DefaultApiResponse<SuccessfulLoginDto> refreshToken(String incomingRefreshToken, HttpServletResponse response) {

        String tokenId = getTokenId(incomingRefreshToken);
        log.info("Token passed into repo {}", tokenId);
        AuthToken maybeToken = authTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Token Not Found"));

        if (Boolean.TRUE.equals(maybeToken.getExpiredOrRevoked())) {
            String email = maybeToken.getUser().getEmailAddress();
            authTokenRepository.findAllByUser_EmailAddress(email)
                    .forEach(t -> t.setExpiredOrRevoked(true));
            authTokenRepository.saveAll(
                    authTokenRepository.findAllByUser_EmailAddress(email)
            );
            return buildErrorResponse("Refresh token reuse detected. All sessions revoked.");
        }

        if (jwtService.isTokenExpired(incomingRefreshToken)) {
            maybeToken.setExpiredOrRevoked(true);
            authTokenRepository.save(maybeToken);
            return buildErrorResponse("Refresh token expired");
        }

        maybeToken.setExpiredOrRevoked(true);
        authTokenRepository.save(maybeToken);

        AppUser user = maybeToken.getUser();
        TokenPair tokenPair = generateTokens(user);
        log.info("The token pair {}", tokenPair.refreshToken);
        saveTokens(user, tokenPair.accessToken, tokenPair.refreshToken);

        setResponseCookie(response, tokenPair);

        SuccessfulLoginDto data = new SuccessfulLoginDto();
        data.setAccessToken(tokenPair.accessToken);
        return buildSuccessResponse("Token refreshed", StatusCodes.ACTION_COMPLETED, data);
    }

    @Override
    public DefaultApiResponse<?> logout(HttpServletRequest req) {
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

        // Revoke *this* token
        storedToken.setExpiredOrRevoked(true);
        authTokenRepository.save(storedToken);
        log.info("Revoked token for user {}", storedToken.getUser().getEmailAddress());

        // Also revoke all other tokens for that user
        var allTokens = authTokenRepository.findAllByUser_EmailAddress(storedToken.getUser().getEmailAddress());
        allTokens.forEach(t -> t.setExpiredOrRevoked(true));
        authTokenRepository.saveAll(allTokens);
        SecurityContextHolder.clearContext();

        return buildSuccessResponse("Logged out successfully.");
    }

    private AppUser createNewUser(OnboardUserDto requestBody) {
        return AppUser.builder()
                .firstName(requestBody.firstName())
                .lastName(requestBody.lastName())
                .emailAddress(requestBody.emailAddress())
                .password(passwordEncoder.encode(requestBody.password()))
                .department(getDepartment(requestBody))
                .collegeBuilding(determineCollegeBuilding(getDepartment(requestBody)))
                .accountVerified(true)
                .role(Role.HOD)
                .build();
    }

    private Department getDepartment(OnboardUserDto requestBody) {
        final String message = String.format("Department with ID %s not found.", requestBody.departmentId());
        return departmentRepository.findById(Long.valueOf(requestBody.departmentId())).orElseThrow(
                () -> new RuntimeException(message)
        );
    }

    private AppUser createNewUser(OnboardUserDto requestBody, Role role) {
        return AppUser.builder()
                .firstName(requestBody.firstName())
                .lastName(requestBody.lastName())
                .emailAddress(requestBody.emailAddress())
                .password(passwordEncoder.encode(requestBody.password()))
                .department(getDepartment(requestBody))
                .collegeBuilding(determineCollegeBuilding(getDepartment(requestBody)))
                .accountVerified(true)
                .role(role)
                .build();
    }

    private AppUserDto mapUserToDto(AppUser user) {
        return AppUserDto.builder()
                .accountVerified(true)
                .department(user.getDepartment())
                .collegeBuilding(determineCollegeBuilding(user.getDepartment()))
                .emailAddress(user.getEmailAddress())
                .build();
    }

    private CollegeBuilding determineCollegeBuilding(Department department) {
        if(department.getCode().equals("DAPU"))
            return CollegeBuilding.builder().code("CMSS_DAPU").build();
        // TODO: Map department to building
        return CollegeBuilding.builder().code("CST").build();
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

    private <T> Optional<DefaultApiResponse<T>> verifyUserAndOtp(VerifyOtpDto requestBody) {
        VerifyOtpResponse responseFromOtpService = otpService.verifyOtp(requestBody.getOneTimePassword(),
                requestBody.getEmail());

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

    private record TokenPair(@NotNull String accessToken, @NotNull String refreshToken) {}
}