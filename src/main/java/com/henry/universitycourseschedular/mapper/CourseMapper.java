package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.Course;
import com.henry.universitycourseschedular.models.Program;
import com.henry.universitycourseschedular.models._dto.CourseRequestDto;
import com.henry.universitycourseschedular.models._dto.CourseResponseDto;
import com.henry.universitycourseschedular.models._dto.CourseUpdateDto;
import com.henry.universitycourseschedular.repositories.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final ProgramRepository programRepository;

    public Course toEntity(CourseRequestDto dto) {
        Program program = programRepository.findByName(dto.programName())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found: " + dto.programName()));

        return Course.builder()
                .code(dto.courseCode())
                .title(dto.courseName())
                .credits(dto.credits())
                .program(program)
                .expectedStudents(dto.expectedStudents())
                .build();
    }

    public CourseResponseDto toDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                course.extractLevel() != null ? course.extractLevel() : 0,
                course.getCredits(),
                course.getProgram().getName(),
                course.getProgram().getName(),
                course.getProgram().getDepartment().getName(),
                course.getProgram().getDepartment().getCollegeBuilding().getCollege().getName(),
                course.getExpectedStudents(),
                course.isGeneralCourse(),
                course.isSportsCourse()
        );
    }

    public void updateCourseFromDto(Course course, CourseUpdateDto dto) {
        if (dto.courseName() != null) {
            course.setTitle(dto.courseName());
        }

        if (dto.credits() != null) {
            course.setCredits(dto.credits());
        }

        if (dto.expectedStudents() != null) {
            course.setExpectedStudents(dto.expectedStudents());
        }
    }

}

