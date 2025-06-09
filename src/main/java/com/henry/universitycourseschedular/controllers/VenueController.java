package com.henry.universitycourseschedular.controllers;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.VenueDto;
import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.services.core.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/venues")
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<DefaultApiResponse<Venue>> create(@Valid @RequestBody VenueDto dto) {
        return ResponseEntity.ok(venueService.createVenue(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<Venue>> update(@PathVariable Long id, @Valid @RequestBody VenueDto dto) {
        return ResponseEntity.ok(venueService.updateVenue(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<?>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.deleteVenue(id));
    }

    @GetMapping
    public ResponseEntity<DefaultApiResponse<List<Venue>>> getAll() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultApiResponse<Venue>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getVenueById(id));
    }
}
