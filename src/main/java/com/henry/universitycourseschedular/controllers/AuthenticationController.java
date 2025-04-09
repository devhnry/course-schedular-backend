package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.dto.*;
import com.henry.universitycourseschedular.services.AuthenticationService;
import com.henry.universitycourseschedular.services.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @PostMapping("/login")
//    public ResponseEntity<DefaultApiResponse<SuccessfulOnboardDto>> login
//            (@RequestBody @Validated OnboardUserDto requestBody){
//        DefaultApiResponse<SuccessfulOnboardDto> response = authenticationService.signUp(requestBody);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

}
