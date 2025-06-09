package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;

public record InviteHodDto (
        @NotBlank String email,
        @NotBlank String departmentId
) { }
