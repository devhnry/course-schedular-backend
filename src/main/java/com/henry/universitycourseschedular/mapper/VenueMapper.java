package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.CollegeBuilding;
import com.henry.universitycourseschedular.models.Venue;
import com.henry.universitycourseschedular.models._dto.VenueRequestDto;
import com.henry.universitycourseschedular.models._dto.VenueResponseDto;
import com.henry.universitycourseschedular.models._dto.VenueSeedDto;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueMapper {

    private final CollegeBuildingRepository collegeBuildingRepository;

    public static void updateFromDto(Venue venue, VenueRequestDto dto, CollegeBuilding building) {
        if (dto == null || venue == null) return;

        venue.setName(dto.name());
        venue.setCapacity(dto.capacity());
        venue.setAvailable(dto.available() != null ? dto.available() : true);
        venue.setCollegeBuilding(building);
    }

    public Venue toEntity(VenueRequestDto dto) {
        CollegeBuilding building = collegeBuildingRepository.findByCode(dto.collegeBuildingCode())
                .orElseThrow(() -> new ResourceNotFoundException("College building not found: " + dto.collegeBuildingCode()));

        return Venue.builder()
                .name(dto.name())
                .capacity(dto.capacity())
                .collegeBuilding(building)
                .available(true) // default unless you're letting frontend toggle this
                .build();
    }

    public Venue toEntity(VenueSeedDto dto, CollegeBuilding building) {
        return Venue.builder()
                .name(dto.getName())
                .capacity(dto.getCapacity())
                .available(dto.getAvailable() == null || dto.getAvailable())
                .collegeBuilding(building)
                .build();
    }

    public VenueResponseDto toDto(Venue venue) {
        CollegeBuilding building = venue.getCollegeBuilding();

        return new VenueResponseDto(
                venue.getId(),
                venue.getName(),
                venue.getCapacity(),
                venue.isAvailable(),
                building != null ? building.getCode() : "General",  // You could also do "GENERAL"
                building != null ? building.getName() : "General"
        );
    }

}

