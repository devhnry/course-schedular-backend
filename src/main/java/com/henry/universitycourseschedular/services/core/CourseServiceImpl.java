package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.CourseMapper;
import com.henry.universitycourseschedular.models._dto.CourseDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models.course.Course;
import com.henry.universitycourseschedular.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public DefaultApiResponse<Course> createCourse(CourseDto dto) {
        try {
            Course course = courseMapper.fromDto(dto);
            courseRepository.save(course);
            return buildSuccessResponse("Course created successfully", StatusCodes.ACTION_COMPLETED, course);
        } catch (Exception e) {
            log.error("Error creating course", e);
            return buildErrorResponse("Failed to create course: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<Course> getCourseById(Long id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            return buildSuccessResponse("Course found", StatusCodes.ACTION_COMPLETED, course);
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<List<Course>> getAllCourses() {
        try {
            return buildSuccessResponse("All courses retrieved", StatusCodes.ACTION_COMPLETED, courseRepository.findAll());
        } catch (Exception e) {
            return buildErrorResponse("Failed to retrieve courses: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<Course> updateCourse(Long id, CourseDto dto) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            courseMapper.updateCourseFromDto(course, dto);
            courseRepository.save(course);
            return buildSuccessResponse("Course updated successfully", StatusCodes.ACTION_COMPLETED, course);
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating course", e);
            return buildErrorResponse("Failed to update course: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<?> deleteCourse(Long id) {
        try {
            if (!courseRepository.existsById(id)) {
                throw new ResourceNotFoundException("Course not found");
            }
            courseRepository.deleteById(id);
            return buildSuccessResponse("Course deleted successfully");
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        }
    }
}

