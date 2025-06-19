package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.CourseRequestDto;
import com.henry.universitycourseschedular.models._dto.CourseResponseDto;
import com.henry.universitycourseschedular.models._dto.CourseUpdateDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;

import java.util.List;

public interface CourseService {
    DefaultApiResponse<CourseResponseDto> createCourse(CourseRequestDto dto);
    DefaultApiResponse<CourseResponseDto> getCourseById(Long id);
    DefaultApiResponse<List<CourseResponseDto>> getAllCourses();
    DefaultApiResponse<CourseResponseDto> updateCourse(Long id, CourseUpdateDto dto);
    DefaultApiResponse<?> deleteCourse(Long id);
}
