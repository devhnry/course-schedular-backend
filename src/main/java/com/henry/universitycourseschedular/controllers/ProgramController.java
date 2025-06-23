package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.ProgramRequestDto;
import com.henry.universitycourseschedular.models._dto.ProgramResponseDto;
import com.henry.universitycourseschedular.services.core.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/programs")
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<ProgramResponseDto>> create(@Valid @RequestBody ProgramRequestDto dto) {
        return ResponseEntity.ok(programService.createProgram(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<ProgramResponseDto>> update(@PathVariable Long id, @Valid @RequestBody ProgramRequestDto dto) {
        return ResponseEntity.ok(programService.updateProgram(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(programService.deleteProgram(id));
    }

    @GetMapping
    public ResponseEntity<DefaultApiResponse<List<ProgramResponseDto>>> getAll() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<ProgramResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(programService.getProgramById(id));
    }
}
