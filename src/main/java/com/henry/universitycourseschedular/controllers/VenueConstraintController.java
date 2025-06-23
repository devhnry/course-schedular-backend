package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models.VenueConstraint;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.services.jobs.VenueConstraintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venue-constraints")
@RequiredArgsConstructor
public class VenueConstraintController {

    private final VenueConstraintService service;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<VenueConstraint>> createConstraint(
            @RequestParam Long venueId,
            @RequestParam Long departmentId,
            @RequestParam boolean restricted) {
        return ResponseEntity.ok(service.createConstraint(venueId, departmentId, restricted));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<VenueConstraint>> updateConstraint(
            @PathVariable Long id,
            @RequestParam boolean restricted) {
        return ResponseEntity.ok(service.updateConstraint(id, restricted));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> deleteConstraint(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteConstraint(id));
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<DefaultApiResponse<List<VenueConstraint>>> getByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(service.getConstraintsByVenue(venueId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<DefaultApiResponse<List<VenueConstraint>>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(service.getConstraintsByDepartment(departmentId));
    }

    @GetMapping
    public ResponseEntity<DefaultApiResponse<List<VenueConstraint>>> getAll() {
        return ResponseEntity.ok(service.getAllConstraints());
    }

    @PostMapping("/generate/building-based")
    public ResponseEntity<DefaultApiResponse<?>> generateBuildingBasedConstraints() {
        return ResponseEntity.ok(service.createBuildingBasedConstraints());
    }

    @PostMapping("/generate/program-specific")
    public ResponseEntity<DefaultApiResponse<?>> generateProgramSpecificConstraints() {
        return ResponseEntity.ok(service.createProgramSpecificConstraints());
    }
}