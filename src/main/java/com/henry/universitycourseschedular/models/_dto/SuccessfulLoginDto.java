package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.enums.Role;

public record SuccessfulLoginDto(
        String userId,
        String fullName,
        Role role,
        String email,
        boolean loginVerified,
        String accessToken,
        String refreshToken,
        String tokenExpirationDuration
) {}

