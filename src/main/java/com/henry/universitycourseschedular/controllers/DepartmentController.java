package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.DepartmentDto;
import com.henry.universitycourseschedular.services.core.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<DepartmentDto>> create(@Valid @RequestBody DepartmentDto dto) {
        return ResponseEntity.ok(departmentService.createDepartment(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<DepartmentDto>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody DepartmentDto dto) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.deleteDepartment(id));
    }

    @GetMapping
    public ResponseEntity<DefaultApiResponse<List<DepartmentDto>>> getAll() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<DepartmentDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

}
