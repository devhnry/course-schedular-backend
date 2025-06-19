package com.henry.universitycourseschedular.models._dto;

import java.time.Instant;

public record OneTimePasswordDto(
        String otpCode,
        Instant generatedTime,
        Instant expirationTime
) {}
