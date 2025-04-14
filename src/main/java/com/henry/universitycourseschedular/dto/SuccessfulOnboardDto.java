package com.henry.universitycourseschedular.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessfulOnboardDto {
    private String emailAddress;
    private String accessToken;
    private String refreshToken;
    private String tokenExpirationDuration;
    private AppUserDto user;
}
