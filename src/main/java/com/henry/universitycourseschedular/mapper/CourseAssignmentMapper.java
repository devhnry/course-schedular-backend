package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.*;
import com.henry.universitycourseschedular.models._dto.CourseAssignmentRequestDto;
import com.henry.universitycourseschedular.models._dto.CourseAssignmentResponseDto;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.CourseRepository;
import com.henry.universitycourseschedular.repositories.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseAssignmentMapper {

    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private final CollegeBuildingRepository buildingRepository;

    public CourseAssignment toEntity(CourseAssignmentRequestDto dto) {
        Course course = courseRepository.findByCourseCode(dto.courseCode())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Program program = course.getProgram();
        Department department = program.getDepartment();
        College college = department.getCollegeBuilding().getCollege();

        List<Lecturer> lecturers = lecturerRepository.findAllByFullNameIn(dto.lecturerNames());

        CollegeBuilding building = null;
        if (dto.overrideBuildingCode() != null) {
            building = buildingRepository.findByCode(dto.overrideBuildingCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Building not found"));
        }

        return CourseAssignment.builder()
                .course(course)
                .program(program)
                .department(department)
                .college(college)
                .lecturers(lecturers)
                .collegeBuilding(building)
                .build();
    }

    public CourseAssignmentResponseDto toDto(CourseAssignment assignment) {
        Course course = assignment.getCourse();

        return new CourseAssignmentResponseDto(
                assignment.getId(),
                course.getCode(),
                course.getTitle(),
                assignment.getProgram().getName(),
                assignment.getDepartment().getCode(),
                assignment.getCollege().getCode(),
                assignment.getLecturers()
                        .stream()
                        .map(Lecturer::getFullName)
                        .toList(),
                assignment.getCollegeBuilding().getCode()
        );
    }
}
