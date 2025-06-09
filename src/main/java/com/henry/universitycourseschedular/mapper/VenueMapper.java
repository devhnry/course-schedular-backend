package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.VenueDto;
import com.henry.universitycourseschedular.models._dto.VenueSeedDto;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Venue;

public class VenueMapper {

    public static Venue fromDto(VenueDto dto, CollegeBuilding building) {
        return Venue.builder()
                .name(dto.name())
                .capacity(dto.capacity())
                .available(true)
                .collegeBuilding(building)
                .build();
    }

    public static void updateFromDto(Venue venue, VenueDto dto, CollegeBuilding building) {
        venue.setName(dto.name());
        venue.setCapacity(dto.capacity());
        venue.setCollegeBuilding(building);
    }

    public static Venue toEntity(VenueSeedDto dto, CollegeBuilding building) {
        return Venue.builder()
                .name(dto.getName())
                .capacity(dto.getCapacity())
                .collegeBuilding(building)
                .build();
    }
}
