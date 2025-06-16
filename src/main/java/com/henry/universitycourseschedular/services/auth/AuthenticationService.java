package com.henry.universitycourseschedular.services.auth;

import com.henry.universitycourseschedular.models._dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

public interface AuthenticationService {
    DefaultApiResponse<SuccessfulOnboardDto> signUp(OnboardUserDto requestBody, String accountFor,
                                                    HttpServletResponse response);
    DefaultApiResponse<SuccessfulLoginDto> login(LoginUserDto requestBody);
    DefaultApiResponse<SuccessfulLoginDto> resendOtpForLogin(String email);
    DefaultApiResponse<SuccessfulLoginDto> verifyLoginOtp(VerifyOtpDto requestBody, HttpServletResponse response);
    DefaultApiResponse<OneTimePasswordDto> sendOtpForPasswordReset(String email);
    DefaultApiResponse<?> verifyPasswordResetOtp(VerifyOtpDto requestBody);
    DefaultApiResponse<?> resetPassword(ResetPasswordDto requestBody);
    @Transactional
    DefaultApiResponse<SuccessfulLoginDto> refreshToken(String incomingRefreshToken, HttpServletResponse response);
    DefaultApiResponse<?> logout(HttpServletRequest request);
}
