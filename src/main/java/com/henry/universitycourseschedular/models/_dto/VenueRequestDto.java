package com.henry.universitycourseschedular.models._dto;

public record VenueRequestDto(
        String name,
        int capacity,
        Boolean available,
        String collegeBuildingCode
) {}

