package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;

public record InviteRequestDto(
        @NotBlank String email,
        @NotBlank String departmentCode
) { }
