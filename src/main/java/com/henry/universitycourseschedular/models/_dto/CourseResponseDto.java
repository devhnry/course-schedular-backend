package com.henry.universitycourseschedular.models._dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private Long id;
    private String courseCode;
    private String courseName;
    private int credits;
    private String programName;
    private String generalBodyName;
    private int expectedStudents;
}
