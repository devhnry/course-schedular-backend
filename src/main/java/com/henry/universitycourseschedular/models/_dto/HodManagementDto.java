package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.enums.InviteStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class HodManagementDto {
    private String userId;
    private String fullName;
    private String emailAddress;
    private String collegeBuildingCode;
    private String departmentCode;
    private Boolean accountVerified;
    private Boolean writeAccess;
    private InviteStatus status;
    private LocalDateTime invitedAt; // when invitation was sent
    private LocalDateTime expiresAt; // invite expiry
}
