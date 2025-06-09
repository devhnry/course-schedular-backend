package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.enums.Role;

public record SuccessfulOnboardDto(
        String userId,
        String fullName,
        Role role,
        String emailAddress,
        String accessToken,
        String tokenExpirationDuration,
        AppUserDto user
) {}
