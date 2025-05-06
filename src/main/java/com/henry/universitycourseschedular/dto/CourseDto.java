package com.henry.universitycourseschedular.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseDto {
    @NotBlank(message = "Course Code is required")
    private String courseCode;

    @NotBlank(message = "Course Name is required")
    private String courseName;

    @Min(1)
    private int units;
}
