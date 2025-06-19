package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models.CollegeBuilding;
import com.henry.universitycourseschedular.models._dto.CollegeBuildingDto;

public class CollegeBuildingMapper {

    public static CollegeBuilding fromDto(CollegeBuildingDto dto) {
        return CollegeBuilding.builder()
                .name(dto.name())
                .code(dto.code())
                .build();
    }

    public static void updateFromDto(CollegeBuilding cb, CollegeBuildingDto dto) {
        cb.setName(dto.name());
        cb.setCode(dto.code());
    }
}
