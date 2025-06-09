package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.CollegeBuildingDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;

import java.util.List;

public interface CollegeBuildingService {
    DefaultApiResponse<CollegeBuilding> create(CollegeBuildingDto dto);
    DefaultApiResponse<CollegeBuilding> update(Long id, CollegeBuildingDto dto);
    DefaultApiResponse<?> delete(Long id);
    DefaultApiResponse<List<CollegeBuilding>> getAll();
    DefaultApiResponse<CollegeBuilding> getById(Long id);
}
