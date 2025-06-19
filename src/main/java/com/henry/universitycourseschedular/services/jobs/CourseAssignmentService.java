package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models._dto.CourseAssignmentRequestDto;
import com.henry.universitycourseschedular.models._dto.CourseAssignmentResponseDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;

import java.util.List;

public interface CourseAssignmentService {
    DefaultApiResponse<CourseAssignmentResponseDto> createAssignment(CourseAssignmentRequestDto body);
    DefaultApiResponse<List<CourseAssignmentResponseDto>> getByDepartment(Long departmentId);

//    DefaultApiResponse<List<CourseAssignmentResponseDto>> getByLecturer(Long lecturerId);

    DefaultApiResponse<CourseAssignmentResponseDto> updateAssignment(Long id, CourseAssignmentRequestDto updatedBody);
    DefaultApiResponse<?> deleteAssignment(Long id);
}
