package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.dto.CourseDto;
import com.henry.universitycourseschedular.dto.CourseResponseDto;
import com.henry.universitycourseschedular.dto.DefaultApiResponse;

import java.util.List;

public interface CourseService {
    DefaultApiResponse<CourseResponseDto> createCourse(CourseDto dto);
    DefaultApiResponse<List<CourseResponseDto>> getAllCourses();
    DefaultApiResponse<CourseResponseDto> getCourseById(String id);
    DefaultApiResponse<CourseResponseDto> updateCourse(String id, CourseDto dto);
    DefaultApiResponse<?> deleteCourse(String id);
}
