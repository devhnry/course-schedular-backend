package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.CourseDto;
import com.henry.universitycourseschedular.models._dto.CourseResponseDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;

import java.util.List;

public interface CourseService {
    DefaultApiResponse<CourseResponseDto> createCourse(CourseDto dto);
    DefaultApiResponse<CourseResponseDto> getCourseById(Long id);
    DefaultApiResponse<List<CourseResponseDto>> getAllCourses();
    DefaultApiResponse<CourseResponseDto> updateCourse(Long id, CourseDto dto);
    DefaultApiResponse<?> deleteCourse(Long id);
}
