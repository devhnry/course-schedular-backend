package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.dto.*;
import com.henry.universitycourseschedular.models.AppUser;
import com.henry.universitycourseschedular.models.AuthToken;
import com.henry.universitycourseschedular.enums.*;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import com.henry.universitycourseschedular.services.*;
import com.henry.universitycourseschedular.utils.OtpRateLimiter;
import com.henry.universitycourseschedular.utils.PasswordValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.henry.universitycourseschedular.services.JwtService.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordValidator passwordValidator;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final OtpRateLimiter otpRateLimiter;
    private final EmailService emailService;

    private record TokenPair(@NotNull String accessToken, @NotNull String refreshToken) {}

    @Value("${email.active}")
    private boolean isEmailActive;

    @Override
    public DefaultApiResponse<SuccessfulOnboardDto> signUp(OnboardUserDto requestBody, String accountFor) {
        DefaultApiResponse<SuccessfulOnboardDto> response = new DefaultApiResponse<>();

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
                tokens.refreshToken,
                String.format("%shrs",tokenExpiration),
                mapUserToDto(user)
        );

        response = buildSuccessResponse("Account created successfully.", StatusCodes.SIGNUP_SUCCESS, data);

        if(isEmailActive){
            if(accountFor.equals("DAPU")){
                // todo - Do Something here
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
    public DefaultApiResponse<SuccessfulLoginDto> verifyLoginOtp(VerifyOtpDto requestBody) {
        try {
            AppUser user = appUserRepository.findByEmailAddress(requestBody.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<DefaultApiResponse<SuccessfulLoginDto>> otpCheckResult = verifyUserAndOtp(requestBody, user);
            if (otpCheckResult.isPresent()) {
                return otpCheckResult.get();
            }

            TokenPair tokens = generateTokens(user);
            saveTokens(user, tokens.accessToken(), tokens.refreshToken());

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

        Optional<DefaultApiResponse<SuccessfulLoginDto>> otpCheckResult = verifyUserAndOtp(requestBody, user);

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


    private static DefaultApiResponse<SuccessfulLoginDto> getSuccessfulLoginDtoDefaultApiResponse(AppUser user, TokenPair tokens) {
        SuccessfulLoginDto data = new SuccessfulLoginDto();
        data.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        data.setUserId(user.getUserId());
        data.setRole(user.getRole());
        data.setEmail(user.getEmailAddress());
        data.setAccessToken(tokens.accessToken());
        data.setRefreshToken(tokens.refreshToken());
        data.setTokenExpirationDuration("24hrs");
        data.setLoginVerified(true);
        data.setRole(user.getRole());


        DefaultApiResponse<SuccessfulLoginDto> response = new DefaultApiResponse<>();
        response.setStatusCode(StatusCodes.OTP_SENT);
        response.setStatusMessage("OTP verified");
        response.setData(data);
        return response;
    }

    private AppUser createNewUser(OnboardUserDto requestBody) {
        return AppUser.builder()
                .firstName(requestBody.firstName())
                .lastName(requestBody.lastName())
                .emailAddress(requestBody.emailAddress())
                .password(passwordEncoder.encode(requestBody.password()))
                .department(requestBody.department())
                .collegeBuilding(determineCollegeBuilding(Department.valueOf(String.valueOf(requestBody.department()))))
                .accountVerified(true)
                .role(Role.HOD)
                .build();
    }
    private AppUser createNewUser(OnboardUserDto requestBody, Role role) {
        return AppUser.builder()
                .firstName(requestBody.firstName())
                .lastName(requestBody.lastName())
                .emailAddress(requestBody.emailAddress())
                .password(passwordEncoder.encode(requestBody.password()))
                .department(requestBody.department())
                .collegeBuilding(determineCollegeBuilding(Department.valueOf(String.valueOf(requestBody.department()))))
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
        if(department.equals(Department.DAPU))
            return CollegeBuilding.CMSS_DAPU;
        // TODO: Map department to building
        return CollegeBuilding.CST;
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
        log.info("Saving AuthTokens...");
        AuthToken token = AuthToken.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredOrRevoked(false)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        authTokenRepository.save(token);
    }

    private <T> Optional<DefaultApiResponse<T>> verifyUserAndOtp(VerifyOtpDto requestBody, AppUser user) {
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
}