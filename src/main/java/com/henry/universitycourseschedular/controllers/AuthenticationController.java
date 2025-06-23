package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.models.AuthToken;
import com.henry.universitycourseschedular.models._dto.*;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import com.henry.universitycourseschedular.services.auth.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;

@Slf4j
@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthTokenRepository authTokenRepository;

    @PostMapping("/auth/onboard")
    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> onboardUser
            (@RequestBody @Validated OnboardRequestUserDto requestBody, HttpServletResponse res){
        DefaultApiResponse<SuccessfulOnboardDto> response = authenticationService.signUp(requestBody, "HOD", res);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/onboard-dapu")
    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> createDAPUAccount
            (@RequestBody @Validated OnboardRequestUserDto requestBody, HttpServletResponse res){
        DefaultApiResponse<SuccessfulOnboardDto> response = authenticationService.signUp(requestBody, "DAPU",res);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<DefaultApiResponse<UnverifiedLoginDto>> login
            (@RequestBody @Validated LoginRequestDto requestBody) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.login(requestBody));
    }

    @PostMapping("/auth/login/verify-otp")
    public ResponseEntity<DefaultApiResponse<SuccessfulLoginDto>> verifyOtp(@RequestBody @Validated OneTimePasswordVerificationDto requestBody, HttpServletResponse res){
        DefaultApiResponse<SuccessfulLoginDto> response = authenticationService.verifyLoginOtp(requestBody,
                res);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/resend-otp")
    public ResponseEntity<DefaultApiResponse<UnverifiedLoginDto>> resendOtp(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.resendOtpForLogin(email));
    }


    @PostMapping("/auth/reset-password/send-otp")
    public ResponseEntity<DefaultApiResponse<OneTimePasswordDto>> sendOtpForPasswordReset(@RequestParam String email){
        DefaultApiResponse<OneTimePasswordDto> response = authenticationService.sendOtpForPasswordReset(email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/reset-password/verify-otp")
    public ResponseEntity<DefaultApiResponse<?>> verifyOtpForPasswordReset(@RequestBody @Validated OneTimePasswordVerificationDto requestBody){
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

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<DefaultApiResponse<String>> refreshToken(
            @CookieValue(name = "jid") String refreshToken, HttpServletResponse res){

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse("Refresh token not provided."));
        }
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.refreshToken(refreshToken, res));
    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultApiResponse<?>> logout(HttpServletRequest req, HttpServletResponse response) {
        DefaultApiResponse<?> res = authenticationService.logout(req, response);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
