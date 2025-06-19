package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Program;
import com.henry.universitycourseschedular.models._dto.ProgramRequestDto;
import com.henry.universitycourseschedular.models._dto.ProgramResponseDto;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProgramMapper {

    private final DepartmentRepository departmentRepository;

    public Program toEntity(ProgramRequestDto dto) {
        Department department = departmentRepository.findByCode(dto.departmentCode())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.departmentCode()));

        return Program.builder()
                .name(dto.name())
                .department(department)
                .build();
    }

    public ProgramResponseDto toDto(Program program) {
        return new ProgramResponseDto(
                program.getId(),
                program.getName(),
                program.getDepartment().getCode(),
                program.getDepartment().getName(),
                program.getDepartment().getCollegeBuilding().getName()
        );
    }

    public void updateFromDto(Program program, ProgramRequestDto dto, Department department) {
        if (program == null || dto == null) return;

        program.setName(dto.name());
        program.setDepartment(department);
    }
}


