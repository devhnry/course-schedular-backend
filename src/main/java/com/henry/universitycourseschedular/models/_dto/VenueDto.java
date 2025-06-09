package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VenueDto(
        @NotBlank(message = "Venue name cannot be blank")
        String name,

        @Min(value = 1, message = "Capacity must be greater than 0")
        int capacity,

        @NotNull(message = "College Building ID is required")
        Long collegeBuildingId
) {}
