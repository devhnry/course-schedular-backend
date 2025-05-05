package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

public interface AuthenticationService {
    DefaultApiResponse<SuccessfulOnboardDto> signUp(OnboardUserDto requestBody, String accountFor);
    DefaultApiResponse<SuccessfulLoginDto> login(LoginUserDto requestBody);
    DefaultApiResponse<SuccessfulLoginDto> verifyLoginOtp(VerifyOtpDto requestBody, HttpServletResponse response);
    DefaultApiResponse<OneTimePasswordDto> sendOtpForPasswordReset(String email);
    DefaultApiResponse<?> verifyPasswordResetOtp(VerifyOtpDto requestBody);
    DefaultApiResponse<?> resetPassword(ResetPasswordDto requestBody);
    @Transactional
    DefaultApiResponse<SuccessfulLoginDto> refreshToken(String incomingRefreshToken, HttpServletResponse response);
    DefaultApiResponse<?> logout(HttpServletRequest request);
}
