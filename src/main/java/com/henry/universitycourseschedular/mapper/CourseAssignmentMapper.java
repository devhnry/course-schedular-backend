package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models._dto.CourseAssignmentDto;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.core.Lecturer;
import com.henry.universitycourseschedular.models.core.Program;
import com.henry.universitycourseschedular.models.course.Course;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.repositories.CourseRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.LecturerRepository;
import com.henry.universitycourseschedular.repositories.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseAssignmentMapper {

    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;

    public CourseAssignment fromDto(CourseAssignmentDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Lecturer lecturer = lecturerRepository.findById(dto.getLecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));
        Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        return CourseAssignment.builder()
                .course(course)
                .lecturer(lecturer)
                .program(program)
                .department(department)
                .isGeneral(dto.isGeneral())
                .build();
    }

    public void updateEntityFromDto(CourseAssignment entity, CourseAssignmentDto dto) {
        entity.setCourse(courseRepository.findById(dto.getCourseId()).orElseThrow());
        entity.setLecturer(lecturerRepository.findById(dto.getLecturerId()).orElseThrow());
        entity.setProgram(programRepository.findById(dto.getProgramId()).orElseThrow());
        entity.setDepartment(departmentRepository.findById(dto.getDepartmentId()).orElseThrow());
        entity.setGeneral(dto.isGeneral());
    }
}
