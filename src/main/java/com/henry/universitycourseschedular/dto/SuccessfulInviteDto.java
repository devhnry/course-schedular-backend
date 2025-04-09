package com.henry.universitycourseschedular.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessfulInviteDto {
    private String email;
    private String inviteToken;
    private boolean inviteVerified;
    private LocalDate inviteDate;
    private LocalDateTime expirationDate;
}
