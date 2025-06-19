package com.henry.universitycourseschedular.models._dto;

public record VenueConstraintRequestDto(
        String venueName,
        String departmentCode,
        boolean restricted
) {}
