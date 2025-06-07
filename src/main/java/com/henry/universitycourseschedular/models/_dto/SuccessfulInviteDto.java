package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessfulInviteDto {
    private String email;
    private String inviteToken;
    private boolean inviteVerified;
    private ZonedDateTime inviteDate;
    private ZonedDateTime expirationDate;
}
