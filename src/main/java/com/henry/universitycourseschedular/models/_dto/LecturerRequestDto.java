package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;

public record LecturerRequestDto(
        @NotBlank String fullName
) {}
