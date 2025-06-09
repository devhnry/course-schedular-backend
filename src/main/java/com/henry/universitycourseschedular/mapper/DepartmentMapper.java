package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.DepartmentDto;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Department;

public class DepartmentMapper {
    public static Department fromDto(DepartmentDto dto, CollegeBuilding building) {
        return Department.builder()
                .name(dto.name())
                .code(dto.code())
                .collegeBuilding(building)
                .build();
    }

    public static void updateFromDto(Department department, DepartmentDto dto, CollegeBuilding building) {
        department.setName(dto.name());
        department.setCode(dto.code());
        department.setCollegeBuilding(building);
    }
}
