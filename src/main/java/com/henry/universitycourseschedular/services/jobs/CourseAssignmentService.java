package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models._dto.CourseAssignmentDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models.course.CourseAssignment;

import java.util.List;

public interface CourseAssignmentService {
    DefaultApiResponse<CourseAssignment> createAssignment(CourseAssignmentDto body);
    DefaultApiResponse<List<CourseAssignment>> getByDepartment(Long departmentId);

    DefaultApiResponse<List<CourseAssignment>> getByLecturer(Long lecturerId);

    DefaultApiResponse<CourseAssignment> updateAssignment(Long id, CourseAssignmentDto updatedBody);
    DefaultApiResponse<?> deleteAssignment(Long id);
}
