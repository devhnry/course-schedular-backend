package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.CourseAssignmentDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.services.jobs.CourseAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/course-assignments")
@RequiredArgsConstructor
public class CourseAssignmentController {

    private final CourseAssignmentService service;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<CourseAssignment>> create(@RequestBody CourseAssignmentDto body) {
        return ResponseEntity.ok(service.createAssignment(body));
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<DefaultApiResponse<?>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(service.getByDepartment(departmentId));
    }

    @GetMapping("/by-department/{lecturerId}")
    public ResponseEntity<DefaultApiResponse<?>> getByLecturer(@PathVariable Long lecturerId) {
        return ResponseEntity.ok(service.getByDepartment(lecturerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<CourseAssignment>> update(
            @PathVariable Long id,
            @RequestBody CourseAssignmentDto body) {
        return ResponseEntity.ok(service.updateAssignment(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteAssignment(id));
    }
}
