package com.henry.universitycourseschedular.models._dto;

public record CourseUpdateDto(
        String courseName,
        Integer credits,
        Integer expectedStudents
) {}

