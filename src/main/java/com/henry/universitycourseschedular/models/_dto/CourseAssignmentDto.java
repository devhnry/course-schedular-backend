package com.henry.universitycourseschedular.models._dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAssignmentDto {
    private Long courseId;
    private Long lecturerId;
    private Long programId;
    private Long departmentId;
    private boolean isGeneral;
}
