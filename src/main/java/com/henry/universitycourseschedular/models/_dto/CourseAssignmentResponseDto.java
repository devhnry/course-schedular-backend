package com.henry.universitycourseschedular.models._dto;

import java.util.List;

public record CourseAssignmentResponseDto(
        Long id,
        String courseCode,
        String courseTitle,
        String programName,
        String departmentCode,
        String collegeCode,
        List<String> lecturerNames,
        String buildingCode  // actual building used (resolved from entity)
) {}

