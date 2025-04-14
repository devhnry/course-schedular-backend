package com.henry.universitycourseschedular.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OneTimePasswordDto {
    private String otpCode;
    @Builder.Default
    private Boolean expired = false;
    private Instant generatedTime;
    private Instant expirationTime;
    private String expirationDuration;
    private AppUserDto user;
}
