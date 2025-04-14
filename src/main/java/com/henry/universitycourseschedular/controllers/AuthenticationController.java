package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.dto.*;
import com.henry.universitycourseschedular.services.AuthenticationService;
import com.henry.universitycourseschedular.services.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final OtpService otpService;

    @PostMapping("/onboard")
    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> onboardUser
            (@RequestBody @Validated OnboardUserDto requestBody){
        DefaultApiResponse<SuccessfulOnboardDto> response = authenticationService.signUp(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultApiResponse<SuccessfulLoginDto>> login
            (@RequestBody @Validated LoginUserDto requestBody) {
        DefaultApiResponse<SuccessfulLoginDto> response = authenticationService.login(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<DefaultApiResponse<SuccessfulLoginDto>> verifyOtp(@RequestBody @Validated VerifyOtpDto requestBody){
        DefaultApiResponse<SuccessfulLoginDto> response = authenticationService.verifyLoginOtp(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/reset-password/send-otp")
    public ResponseEntity<DefaultApiResponse<OneTimePasswordDto>> sendOtpForPasswordReset(@RequestParam String email){
        DefaultApiResponse<OneTimePasswordDto> response = authenticationService.sendOtpForPasswordReset(email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/reset-password/verify-otp")
    public ResponseEntity<DefaultApiResponse<?>> verifyOtpForPasswordReset(@RequestBody @Validated VerifyOtpDto requestBody){
        DefaultApiResponse<?> response = authenticationService.verifyPasswordResetOtp(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<DefaultApiResponse<?>> resetPassword(@RequestBody ResetPasswordDto requestBody){
        DefaultApiResponse<?> response = authenticationService.resetPassword(requestBody);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultApiResponse<?>> logout(HttpServletRequest request, HttpServletResponse response) {
        DefaultApiResponse<?> res = authenticationService.logout(request, response);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
