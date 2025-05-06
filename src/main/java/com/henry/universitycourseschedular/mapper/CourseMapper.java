package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.dto.CourseDto;
import com.henry.universitycourseschedular.dto.CourseResponseDto;
import com.henry.universitycourseschedular.enums.Department;
import com.henry.universitycourseschedular.models.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    public Course toEntity(CourseDto dto, Department department) {
        return Course.builder()
                .courseCode(dto.getCourseCode())
                .courseName(dto.getCourseName())
                .units(dto.getUnits())
                .department(department)
                .build();
    }

    public void updateEntity(Course course, CourseDto dto) {
        course.setCourseCode(dto.getCourseCode());
        course.setCourseName(dto.getCourseName());
        course.setUnits(dto.getUnits());
    }

    public CourseResponseDto toDto(Course course) {
        return CourseResponseDto.builder()
                .courseId(course.getCourseId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .units(course.getUnits())
                .department(course.getDepartment())
                .build();
    }
}
