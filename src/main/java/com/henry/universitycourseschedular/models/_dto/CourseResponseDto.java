package com.henry.universitycourseschedular.models._dto;

public record CourseResponseDto(
        Long id,
        String courseCode,
        String courseName,
        int level,
        int credits,
        String programCode,
        String programName,
        String departmentName,
        String collegeName,
        Integer expectedStudents,
        boolean isGeneralCourse,
        boolean isSportsCourse
) {}
