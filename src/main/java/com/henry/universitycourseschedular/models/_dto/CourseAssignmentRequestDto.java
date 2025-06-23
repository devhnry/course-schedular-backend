package com.henry.universitycourseschedular.models._dto;

import java.util.List;

public record CourseAssignmentRequestDto(
        String courseCode,                    // e.g. "CVE312"
        List<String> lecturerNames,          // e.g. ["Dr. Yomi", "Engr. Cletus"]
        String overrideBuildingCode          // optional (can be null)
) {}

