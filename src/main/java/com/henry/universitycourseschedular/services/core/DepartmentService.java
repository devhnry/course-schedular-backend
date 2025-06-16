package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {
    DefaultApiResponse<DepartmentDto> createDepartment(DepartmentDto dto);
    DefaultApiResponse<DepartmentDto> updateDepartment(Long id, DepartmentDto dto);
    DefaultApiResponse<?> deleteDepartment(Long id);
    DefaultApiResponse<List<DepartmentDto>> getAllDepartments();
    DefaultApiResponse<DepartmentDto> getDepartmentById(Long id);
}
