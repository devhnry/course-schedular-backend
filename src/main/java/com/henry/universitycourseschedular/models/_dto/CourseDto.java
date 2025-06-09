package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseDto(
        @NotBlank String courseCode,
        @NotBlank String courseName,
        @Min(1) int credits,
        @NotNull Long programId,
        @NotNull Long generalBodyId,
        @Min(0) int expectedStudents
) {}
