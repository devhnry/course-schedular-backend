package com.henry.universitycourseschedular.models._dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.henry.universitycourseschedular.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvitationDto {
    private String invitationId;
    private String emailAddress;
    private Role role;
    private String departmentName;
    private String token;
    private ZonedDateTime createdAt;
    private ZonedDateTime expiryDate;
    private boolean expiredOrUsed;
}
