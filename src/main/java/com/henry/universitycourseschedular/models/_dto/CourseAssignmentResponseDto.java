package com.henry.universitycourseschedular.models._dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAssignmentResponseDto {
    private Long id;
    private String courseCode;
    private String courseName;
    private String lecturerName;
    private String programName;
    private String departmentName;
    private boolean isGeneral;
}
