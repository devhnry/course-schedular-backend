package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.CourseDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models.course.Course;

import java.util.List;

public interface CourseService {
    DefaultApiResponse<Course> createCourse(CourseDto dto);
    DefaultApiResponse<Course> getCourseById(Long id);
    DefaultApiResponse<List<Course>> getAllCourses();
    DefaultApiResponse<Course> updateCourse(Long id, CourseDto dto);
    DefaultApiResponse<?> deleteCourse(Long id);
}
