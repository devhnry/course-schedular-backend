package com.henry.universitycourseschedular.services.auth;

import com.henry.universitycourseschedular.models._dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

public interface AuthenticationService {
    DefaultApiResponse<SuccessfulOnboardDto> signUp(OnboardRequestUserDto requestBody, String accountFor,
                                                    HttpServletResponse response);
    DefaultApiResponse<UnverifiedLoginDto> login(LoginRequestDto requestBody);
    DefaultApiResponse<UnverifiedLoginDto> resendOtpForLogin(String email);
    DefaultApiResponse<SuccessfulLoginDto> verifyLoginOtp(OneTimePasswordVerificationDto requestBody, HttpServletResponse response);
    DefaultApiResponse<OneTimePasswordDto> sendOtpForPasswordReset(String email);
    DefaultApiResponse<?> verifyPasswordResetOtp(OneTimePasswordVerificationDto requestBody);
    DefaultApiResponse<?> resetPassword(ResetPasswordDto requestBody);
    @Transactional
    DefaultApiResponse<String> refreshToken(String incomingRefreshToken, HttpServletResponse response);
    DefaultApiResponse<?> logout(HttpServletRequest req, HttpServletResponse res);
}
