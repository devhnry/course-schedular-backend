package com.henry.universitycourseschedular.models._dto;

public record CourseRequestDto(
        String courseCode,
        String courseName,
        int credits,
        String programName,
        Integer expectedStudents
) {}

