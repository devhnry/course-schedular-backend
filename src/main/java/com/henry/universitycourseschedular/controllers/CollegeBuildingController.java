package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.CollegeBuildingDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.services.core.CollegeBuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buildings")
public class CollegeBuildingController {

    private final CollegeBuildingService service;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<CollegeBuilding>> create(@Valid @RequestBody CollegeBuildingDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<CollegeBuilding>> update(@PathVariable Long id, @Valid @RequestBody CollegeBuildingDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

    @GetMapping
    public ResponseEntity<DefaultApiResponse<List<CollegeBuilding>>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<CollegeBuilding>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
