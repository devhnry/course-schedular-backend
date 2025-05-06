package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.dto.CourseDto;
import com.henry.universitycourseschedular.dto.CourseResponseDto;
import com.henry.universitycourseschedular.dto.DefaultApiResponse;
import com.henry.universitycourseschedular.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<CourseResponseDto>> createCourse(@RequestBody @Valid CourseDto dto) {
        DefaultApiResponse<CourseResponseDto> response = courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<DefaultApiResponse<List<CourseResponseDto>>> getAllCourses() {
        DefaultApiResponse<List<CourseResponseDto>> response = courseService.getAllCourses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<CourseResponseDto>> getCourseById(@PathVariable String id) {
        DefaultApiResponse<CourseResponseDto> response = courseService.getCourseById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<CourseResponseDto>> updateCourse(
            @PathVariable String id, @RequestBody @Valid CourseDto dto) {
        DefaultApiResponse<CourseResponseDto> response = courseService.updateCourse(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> deleteCourse(@PathVariable String id) {
        DefaultApiResponse<?> response = courseService.deleteCourse(id);
        return ResponseEntity.ok(response);
    }
}

