package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.enums.InviteStatus;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data @Builder
public class HodManagementDto {
    private String userId;           // null if not signed up yet
    private String emailAddress;
    private String fullName;
    private String departmentId;
    private String departmentName;
    private Boolean accountVerified; // false until they verify
    private Boolean writeAccess;     // from AppUser, null if not onboarded
    private InviteStatus status;     // see enum below
    private ZonedDateTime invitedAt; // when invitation was sent
    private ZonedDateTime expiresAt; // invite expiry
}
