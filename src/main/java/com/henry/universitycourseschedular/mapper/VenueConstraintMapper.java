package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Venue;
import com.henry.universitycourseschedular.models.VenueConstraint;
import com.henry.universitycourseschedular.models._dto.VenueConstraintRequestDto;
import com.henry.universitycourseschedular.models._dto.VenueConstraintResponseDto;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueConstraintMapper {

    private final VenueRepository venueRepository;
    private final DepartmentRepository departmentRepository;

    public VenueConstraint toEntity(VenueConstraintRequestDto dto) {
        Venue venue = venueRepository.findByName(dto.venueName())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + dto.venueName()));

        Department dept = departmentRepository.findByCode(dto.departmentCode())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.departmentCode()));

        return VenueConstraint.builder()
                .venue(venue)
                .preferredDepartment(dept)
                .restricted(dto.restricted())
                .build();
    }

    public VenueConstraintResponseDto toDto(VenueConstraint constraint) {
        return new VenueConstraintResponseDto(
                constraint.getId(),
                constraint.getVenue().getName(),
                constraint.getPreferredDepartment().getCode(),
                constraint.getPreferredDepartment().getName(),
                constraint.isRestricted(),
                constraint.getConstraintType()
        );
    }
}
