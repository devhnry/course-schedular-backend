package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProgramDto(
        @NotBlank(message = "Program name cannot be blank")
        String name,

        @NotBlank(message = "Program code cannot be blank")
        String code,

        @NotNull(message = "Department ID is required")
        Long departmentId
) {}
