package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;

public record CollegeBuildingDto(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Code is required")
        String code,
        @NotBlank(message = "College Code is required")
        String collegeCode
) {}
