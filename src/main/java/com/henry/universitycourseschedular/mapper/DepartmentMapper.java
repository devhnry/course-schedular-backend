package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.DepartmentDto;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public static Department fromDto(DepartmentDto dto, CollegeBuilding building) {
        return Department.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .collegeBuilding(building)
                .build();
    }

    public static void updateFromDto(Department department, DepartmentDto dto, CollegeBuilding building) {
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setCollegeBuilding(building);
    }

    // Map from Department entity to DTO
    public DepartmentDto toDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());

        CollegeBuilding collegeBuilding = department.getCollegeBuilding();
        if (collegeBuilding != null) {
            dto.setCollegeBuildingId(collegeBuilding.getId());
            dto.setCollegeBuildingName(collegeBuilding.getName());
        }

        return dto;
    }
}
