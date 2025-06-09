package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.LecturerDto;
import com.henry.universitycourseschedular.models.core.Lecturer;
import com.henry.universitycourseschedular.services.core.LectureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lecturers")
public class LectureController {

    private final LectureService lecturerService;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<Lecturer>> create(@Valid @RequestBody LecturerDto dto) {
        return ResponseEntity.ok(lecturerService.createLecturer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<Lecturer>> update(@PathVariable Long id,
                                                               @Valid @RequestBody LecturerDto dto) {
        return ResponseEntity.ok(lecturerService.updateLecturer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok().body(lecturerService.deleteLecturer(id));
    }

    @GetMapping
    public ResponseEntity<DefaultApiResponse<List<Lecturer>>> getAll() {
        return ResponseEntity.ok(lecturerService.getAllLecturers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<Lecturer>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lecturerService.getLecturerById(id));
    }
}
