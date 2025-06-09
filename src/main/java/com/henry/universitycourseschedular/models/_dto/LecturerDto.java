package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.enums.Title;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public record LecturerDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Enumerated(EnumType.STRING) Title title,
        String email
) {}
