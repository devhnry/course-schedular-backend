package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.dto.SuccessfulOnboardDto;
import com.henry.universitycourseschedular.enums.CollegeBuilding;
import com.henry.universitycourseschedular.enums.ContextType;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import com.henry.universitycourseschedular.services.AuthenticationService;
import com.henry.universitycourseschedular.dto.DefaultApiResponse;
import com.henry.universitycourseschedular.dto.OnboardUserDto;
import com.henry.universitycourseschedular.entity.AuthToken;
import com.henry.universitycourseschedular.entity.AppUser;
import com.henry.universitycourseschedular.services.JwtService;
import com.henry.universitycourseschedular.services.OtpService;
import com.henry.universitycourseschedular.utils.PasswordValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;

@Service @RequiredArgsConstructor @Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final PasswordValidator passwordValidator;
    private final JwtService jwtService;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
    }

    private record accessTokenAndRefreshToken(@NotNull String accessToken, @NotNull String refreshToken) {}

    @Override
    public DefaultApiResponse<SuccessfulOnboardDto> signUp(OnboardUserDto requestBody) {
        HttpSession session = getCurrentRequest().getSession();
        DefaultApiResponse<SuccessfulOnboardDto> response = new DefaultApiResponse<>();
        SuccessfulOnboardDto data = new SuccessfulOnboardDto();

        boolean userAlreadyExists = appUserRepository.existsByEmailAddress(requestBody.emailAddress());
        if(userAlreadyExists){
            log.info("Customer with email {} already exists.", requestBody.emailAddress());
            response.setStatusCode(99);
            response.setStatusMessage("HOD already exists on the system.");
            return response;
        }

        boolean passwordStrengthValid = passwordValidator.verifyPasswordStrength(requestBody.password());
        if(!passwordStrengthValid){
            response.setStatusCode(99);
            response.setStatusMessage("Password strength invalid.");
            return response;
        }
        boolean passwordsMatch = requestBody.password().equals(requestBody.confirmPassword());
        if(!passwordsMatch){
            response.setStatusCode(99);
            response.setStatusMessage("Passwords do not match.");
            return response;
        }

        Boolean isVerified = (Boolean) session.getAttribute("inviteVerified");
        log.info("Is User Verified: {}", isVerified);
        log.info("Request body verification: {}", requestBody.inviteVerified());

        if (!Boolean.TRUE.equals(isVerified) || !requestBody.inviteVerified()) {
            response.setStatusCode(99);
            response.setStatusMessage("Invitation verification invalid: contact support or request for a new " +
                    "verification link");
            return response;
        }

        AppUser user = generateHODProfile(requestBody, requestBody.emailAddress());
        accessTokenAndRefreshToken tokens = generateAccessTokenAndRefreshToken(user);
        saveCustomerToken(user, tokens.accessToken, tokens.refreshToken);

        data.setEmailAddress(user.getEmailAddress());
        data.setAccountVerified(user.getAccountVerified());
        data.setOneTimePassword("");

        otpService.sendOtp(requestBody.emailAddress(), ContextType.ONBOARDING);

        response.setStatusCode(00);
        response.setStatusMessage("Account created successfully: Verify OTP");
        response.setData(data);
        return response;
    }

    private AppUser generateHODProfile(OnboardUserDto requestBody, String email){
        /** todo: Include check for department to determine collegeBuilding */

        return AppUser.builder()
                .firstName(requestBody.firstName())
                .lastName(requestBody.lastName())
                .password(passwordEncoder.encode(requestBody.password()))
                .emailAddress(email)
                .department(requestBody.department())
                .collegeBuilding(CollegeBuilding.CST)
                .accountVerified(false)
                .build();
    }

    private accessTokenAndRefreshToken generateAccessTokenAndRefreshToken(AppUser user){
        HashMap<String, Object> refreshTokenClaims = generateRefreshTokenClaims(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenClaims);

        return new accessTokenAndRefreshToken(accessToken, refreshToken);
    }

    private @NotNull HashMap<String, Object> generateRefreshTokenClaims(AppUser user){
        // Log the process of generating refresh token claims
        log.info("Generating Refresh Token Claims");

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmailAddress());
        claims.put("customerId", user.getUserId());
        return claims;
    }

    private void saveCustomerToken(AppUser user, String jwtToken, String refreshToken){
        // Log the process of saving tokens
        log.info("Saving tokens for customer {}", user.getEmailAddress());

        // Save the generated access and refresh tokens for the customer
        AuthToken token = AuthToken.builder()
                .user(user)
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .expiredOrRevoked(false)
                .build();
        authTokenRepository.save(token);

        // Log successful token saving
        log.info("Saved Access and Refresh tokens for customer {}", user.getEmailAddress());
    }
}
