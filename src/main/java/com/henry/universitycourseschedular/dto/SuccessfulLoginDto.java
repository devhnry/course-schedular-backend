package com.henry.universitycourseschedular.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class SuccessfulLoginDto {
    private String hodEmail;
    private Boolean loginVerified;
    private String accessToken;
    private String refreshToken;
    private String tokenExpirationDuration;
    private OneTimePasswordDto oneTimePassword;
}
