package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Lecturer;
import com.henry.universitycourseschedular.models._dto.LecturerRequestDto;
import com.henry.universitycourseschedular.models._dto.LecturerResponseDto;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LecturerMapper {

    private final DepartmentRepository departmentRepository;

    public Lecturer toEntity(LecturerRequestDto dto, Department department) {
        return Lecturer.builder()
                .fullName(dto.fullName())
                .department(department)
                .build();
    }

    public LecturerResponseDto toDto(Lecturer lecturer) {
        return new LecturerResponseDto(
                lecturer.getId(),
                lecturer.getFullName(),
                lecturer.getDepartment().getCode(),
                lecturer.getDepartment().getName(),
                lecturer.getDepartment().getCollegeBuilding().getCollege().getName()
        );
    }

    public void updateLecturerFromDto(Lecturer entity, LecturerRequestDto dto, Department department) {
        if (entity == null || dto == null) return;
        entity.setFullName(dto.fullName());
        entity.setDepartment(department);
    }
}


