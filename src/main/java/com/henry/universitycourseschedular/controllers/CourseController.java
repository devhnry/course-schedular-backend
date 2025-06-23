package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.CourseRequestDto;
import com.henry.universitycourseschedular.models._dto.CourseResponseDto;
import com.henry.universitycourseschedular.models._dto.CourseUpdateDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.services.core.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<CourseResponseDto>> create(@Valid @RequestBody CourseRequestDto dto) {
        return ResponseEntity.ok(courseService.createCourse(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<CourseResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping
    public ResponseEntity<DefaultApiResponse<List<CourseResponseDto>>> getAll() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<CourseResponseDto>> update(
            @PathVariable Long id, @Valid @RequestBody CourseUpdateDto dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.deleteCourse(id));
    }
}