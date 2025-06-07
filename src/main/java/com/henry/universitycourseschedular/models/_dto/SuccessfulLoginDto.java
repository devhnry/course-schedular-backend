package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.henry.universitycourseschedular.enums.Role;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
public class SuccessfulLoginDto {
    private String userId;
    private String fullName;
    private Role role;
    private String email;
    private Boolean loginVerified;
    private String accessToken;
    private String refreshToken;
    private String tokenExpirationDuration;
    private OneTimePasswordDto oneTimePassword;
}
