package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.CourseDto;
import com.henry.universitycourseschedular.models.course.Course;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.LecturerRepository;
import com.henry.universitycourseschedular.repositories.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final LecturerRepository lecturerRepo;
    private final ProgramRepository programRepo;
    private final DepartmentRepository departmentRepo;

    public Course fromDto(CourseDto dto) {
        return Course.builder()
                .courseCode(dto.courseCode())
                .courseName(dto.courseName())
                .credits(dto.credits())
                .program(programRepo.findById(dto.programId())
                        .orElseThrow(() -> new RuntimeException("Program not found")))
                .expectedStudents(dto.expectedStudents())
                .courseType(dto.courseType())
                .build();
    }

    public void updateCourseFromDto(Course course, CourseDto dto) {
        course.setCourseCode(dto.courseCode());
        course.setCourseName(dto.courseName());
        course.setCredits(dto.credits());
        course.setExpectedStudents(dto.expectedStudents());
        course.setCourseType(dto.courseType());
        course.setProgram(programRepo.findById(dto.programId())
                .orElseThrow(() -> new RuntimeException("Program not found")));
    }
}
