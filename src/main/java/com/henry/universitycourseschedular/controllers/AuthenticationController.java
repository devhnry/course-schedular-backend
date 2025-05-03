package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.dto.*;
import com.henry.universitycourseschedular.models.AuthToken;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import com.henry.universitycourseschedular.services.AuthenticationService;
import com.henry.universitycourseschedular.services.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final HttpServletRequest httpServletRequest;
    private final AuthTokenRepository authTokenRepository;
    private final OtpService otpService;

    @PostMapping("/auth/onboard")
    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> onboardUser
            (@RequestBody @Validated OnboardUserDto requestBody){
        DefaultApiResponse<SuccessfulOnboardDto> response = authenticationService.signUp(requestBody, "HOD");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/onboard-dapu")
    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> createDAPUAccount
            (@RequestBody @Validated OnboardUserDto requestBody){
        DefaultApiResponse<SuccessfulOnboardDto> response = authenticationService.signUp(requestBody, "DAPU");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<DefaultApiResponse<SuccessfulLoginDto>> login
            (@RequestBody @Validated LoginUserDto requestBody) {
        DefaultApiResponse<SuccessfulLoginDto> response = authenticationService.login(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/login/verify-otp")
    public ResponseEntity<DefaultApiResponse<SuccessfulLoginDto>> verifyOtp(@RequestBody @Validated VerifyOtpDto requestBody){
        DefaultApiResponse<SuccessfulLoginDto> response = authenticationService.verifyLoginOtp(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/reset-password/send-otp")
    public ResponseEntity<DefaultApiResponse<OneTimePasswordDto>> sendOtpForPasswordReset(@RequestParam String email){
        DefaultApiResponse<OneTimePasswordDto> response = authenticationService.sendOtpForPasswordReset(email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/reset-password/verify-otp")
    public ResponseEntity<DefaultApiResponse<?>> verifyOtpForPasswordReset(@RequestBody @Validated VerifyOtpDto requestBody){
        DefaultApiResponse<?> response = authenticationService.verifyPasswordResetOtp(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<DefaultApiResponse<?>> resetPassword(@RequestBody ResetPasswordDto requestBody){
        DefaultApiResponse<?> response = authenticationService.resetPassword(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/auth-check")
    public ResponseEntity<DefaultApiResponse<?>> authCheck(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        AuthToken authToken = authTokenRepository.findByAccessToken(authHeader.substring(7)).orElseThrow(
                () -> new RuntimeException("Access Token Not Found")
        );
        DefaultApiResponse<?> response = new DefaultApiResponse<>(StatusCodes.ACTION_COMPLETED, "Token is still " +
                "valid!", authToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultApiResponse<?>> logout() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new DefaultApiResponse<>(StatusCodes.GENERIC_FAILURE,"You are not logged in", null));
        }

        DefaultApiResponse<?> res = authenticationService.logout(httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
