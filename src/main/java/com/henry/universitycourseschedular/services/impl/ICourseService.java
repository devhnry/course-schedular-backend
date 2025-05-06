package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.dto.CourseDto;
import com.henry.universitycourseschedular.dto.CourseResponseDto;
import com.henry.universitycourseschedular.dto.DefaultApiResponse;
import com.henry.universitycourseschedular.enums.Department;
import com.henry.universitycourseschedular.mapper.CourseMapper;
import com.henry.universitycourseschedular.models.Course;
import com.henry.universitycourseschedular.repositories.CourseRepository;
import com.henry.universitycourseschedular.services.CourseService;
import com.henry.universitycourseschedular.services.UserContextService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @Slf4j @RequiredArgsConstructor
public class ICourseService implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper mapper;
    private final UserContextService userContextService;

    @Override
    public DefaultApiResponse<CourseResponseDto> createCourse(CourseDto dto) {
        try {
            Department department = userContextService.getCurrentUserDepartment();
            Course course = mapper.toEntity(dto, department);
            courseRepository.save(course);
            return buildSuccessResponse("Course Added", StatusCodes.ACTION_COMPLETED, mapper.toDto(course));
        } catch (Exception e) {
            log.error("Unable to create lecturer", e);
            return buildErrorResponse("An Error Occurred");
        }
    }

    @Override
    public DefaultApiResponse<List<CourseResponseDto>> getAllCourses() {
        try {
            Department department = userContextService.getCurrentUserDepartment();
            List<CourseResponseDto> courseDtos = courseRepository.findAllByDepartment(department)
                    .stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
            return buildSuccessResponse("All Courses Listed", StatusCodes.ACTION_COMPLETED, courseDtos);
        } catch (Exception e) {
            return buildErrorResponse(String.format("An Error Occurred %s", e.getMessage()));
        }
    }

    @Override
    public DefaultApiResponse<CourseResponseDto> getCourseById(String id) {
        try {
            Course course = courseRepository.findByCourseId(id).orElseThrow(() -> new EntityNotFoundException(
                    "Course not found"));
            return buildSuccessResponse("Lecturer Found", StatusCodes.ACTION_COMPLETED, mapper.toDto(course));
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<CourseResponseDto> updateCourse(String id, CourseDto dto) {
        Course course = courseRepository.findByCourseId(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));

        mapper.updateEntity(course, dto);
        courseRepository.save(course);
        return buildSuccessResponse("Lecturer Updated Successfully", StatusCodes.ACTION_COMPLETED, mapper.toDto(course));
    }

    @Override
    public DefaultApiResponse<?> deleteCourse(String id) {
        try {
            if (!courseRepository.existsById(id)) {
                throw new EntityNotFoundException("Lecturer not found");
            }
            courseRepository.deleteById(id);
            return buildSuccessResponse("Lecturer Deleted Successfully");
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        }
    }
}
