package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.ProgramDto;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.core.Program;

public class ProgramMapper {

    public static Program fromDto(ProgramDto dto, Department department) {
        return Program.builder()
                .name(dto.name())
                .code(dto.code())
                .department(department)
                .build();
    }

    public static void updateFromDto(Program program, ProgramDto dto, Department department) {
        program.setName(dto.name());
        program.setCode(dto.code());
        program.setDepartment(department);
    }
}
