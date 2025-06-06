package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.henry.universitycourseschedular.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessfulOnboardDto {
    private String userId;
    private String fullName;
    private Role role;
    private String emailAddress;
    private String accessToken;
    private String tokenExpirationDuration;
    private AppUserDto user;
}
