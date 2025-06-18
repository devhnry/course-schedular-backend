package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProgramDto(
        @NotBlank(message = "Program name cannot be blank")
        String name,

        String code,

        @NotNull(message = "Department ID is required")
        Long departmentId
) {}
