package com.henry.universitycourseschedular.models._dto;

public record UnverifiedLoginDto(
        String email,
        boolean loginVerified,
        OneTimePasswordDto oneTimePassword
) {}

