package com.henry.universitycourseschedular.models._dto;

public record VenueResponseDto(
        Long id,
        String name,
        int capacity,
        boolean available,
        String collegeBuildingCode,
        String collegeBuildingName
) {}

