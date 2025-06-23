package com.henry.universitycourseschedular.models._dto;

public record VenueConstraintResponseDto(
        Long id,
        String venueName,
        String departmentCode,
        String departmentName,
        boolean restricted,
        String constraintType
) {}

