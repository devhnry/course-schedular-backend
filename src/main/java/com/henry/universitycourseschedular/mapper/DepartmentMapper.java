package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.CollegeBuilding;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models._dto.DepartmentRequestDto;
import com.henry.universitycourseschedular.models._dto.DepartmentResponseDto;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentMapper {

    private final CollegeBuildingRepository collegeBuildingRepository;

    public Department toEntity(DepartmentRequestDto dto) {
        CollegeBuilding building = collegeBuildingRepository.findByCode(dto.collegeBuildingCode())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + dto.collegeBuildingCode()));

        return Department.builder()
                .name(dto.name())
                .code(dto.code())
                .collegeBuilding(building)
                .build();
    }

    public DepartmentResponseDto toDto(Department department) {
        return new DepartmentResponseDto(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getCollegeBuilding().getCode(),
                department.getCollegeBuilding().getName()
        );
    }

    public void updateFromDto(Department department, DepartmentRequestDto dto, CollegeBuilding building) {
        if (dto.name() != null) {
            department.setName(dto.name());
        }

        if (dto.code() != null) {
            department.setCode(dto.code());
        }

        if (building != null) {
            department.setCollegeBuilding(building);
        }
    }

}
