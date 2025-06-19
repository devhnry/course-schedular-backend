package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto (
        @NotBlank String email,
        @NotBlank String password
) { }
