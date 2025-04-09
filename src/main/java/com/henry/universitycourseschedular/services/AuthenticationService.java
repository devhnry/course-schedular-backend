package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.dto.DefaultApiResponse;
import com.henry.universitycourseschedular.dto.OnboardUserDto;
import com.henry.universitycourseschedular.dto.SuccessfulOnboardDto;
import com.henry.universitycourseschedular.entity.AuthToken;

public interface AuthenticationService {
    DefaultApiResponse<SuccessfulOnboardDto> signUp(OnboardUserDto requestBody);
//    DefaultApiResponse<> sendOtp(String invitedEmail);
//    DefaultApiResponse<> verifyOtp(String invitedEmail, String oneTimePassword);
//    DefaultApiResponse<> login();
//    DefaultApiResponse<> resetPassword();
//    DefaultApiResponse<> logout();
}
