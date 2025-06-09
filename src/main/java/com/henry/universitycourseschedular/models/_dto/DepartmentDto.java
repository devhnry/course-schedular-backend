package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartmentDto(
        @NotBlank(message = "Department name cannot be blank")
        String name,

        @NotBlank(message = "Department code cannot be blank")
        String code,

        @NotNull(message = "College building ID is required")
        Long collegeBuildingId
) { }