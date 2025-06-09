package com.henry.universitycourseschedular.models._dto;

import com.henry.universitycourseschedular.enums.CourseType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseDto(
        @NotBlank String courseCode,
        @NotBlank String courseName,
        @Min(1) int credits,
        @NotNull Long programId,
        @Min(0) int expectedStudents,
        @NotNull @Enumerated(EnumType.STRING) CourseType courseType
) {}
