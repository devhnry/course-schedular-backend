package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.CourseMapper;
import com.henry.universitycourseschedular.models.Course;
import com.henry.universitycourseschedular.models._dto.CourseRequestDto;
import com.henry.universitycourseschedular.models._dto.CourseResponseDto;
import com.henry.universitycourseschedular.models._dto.CourseUpdateDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
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
    public DefaultApiResponse<CourseResponseDto> createCourse(CourseRequestDto dto) {
        try {
            Course course = courseMapper.toEntity(dto);
            courseRepository.save(course);
            return buildSuccessResponse("Course created successfully", StatusCodes.ACTION_COMPLETED, courseMapper.toDto(course));
        } catch (Exception e) {
            log.error("Error creating course", e);
            return buildErrorResponse("Failed to create course: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<CourseResponseDto> getCourseById(Long id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            return buildSuccessResponse("Course found", StatusCodes.ACTION_COMPLETED, courseMapper.toDto(course));
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<List<CourseResponseDto>> getAllCourses() {
        try {
            List<CourseResponseDto> dtos = courseRepository.findAll()
                    .stream().map(courseMapper::toDto)
                    .toList();
            return buildSuccessResponse("All courses retrieved", StatusCodes.ACTION_COMPLETED, dtos);
        } catch (Exception e) {
            return buildErrorResponse("Failed to retrieve courses: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<CourseResponseDto> updateCourse(Long id, CourseUpdateDto dto) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            courseMapper.updateCourseFromDto(course, dto);
            courseRepository.save(course);

            courseMapper.updateCourseFromDto(course, dto);
            Course updated = courseRepository.save(course);

            return buildSuccessResponse("Course updated successfully", StatusCodes.ACTION_COMPLETED, courseMapper.toDto(updated));
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

