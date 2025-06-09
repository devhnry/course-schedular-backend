package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.VenueDTO;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Venue;

public class VenueMapper {

    public static Venue toEntity(VenueDTO dto, CollegeBuilding building) {
        return Venue.builder()
                .name(dto.getName())
                .capacity(dto.getCapacity())
                .collegeBuilding(building)
                .build();
    }
}
