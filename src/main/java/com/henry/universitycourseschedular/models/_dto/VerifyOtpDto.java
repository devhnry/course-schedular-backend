package com.henry.universitycourseschedular.models._dto;

public record VerifyOtpDto(
        String email,
        String oneTimePassword
) {}
