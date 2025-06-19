package com.henry.universitycourseschedular.models._dto;

public record OneTimePasswordVerificationDto(
        String email,
        String oneTimePassword
) {}
