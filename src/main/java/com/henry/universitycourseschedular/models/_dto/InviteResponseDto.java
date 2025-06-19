package com.henry.universitycourseschedular.models._dto;

import java.time.LocalDateTime;

public record InviteResponseDto(
        String invitationId,
        String emailAddress,
        String role,
        String departmentCode,
        String departmentName,
        boolean expiredOrUsed,
        LocalDateTime createdAt,
        LocalDateTime expiryDate
) {}

