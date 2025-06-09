package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.DepartmentDto;
import com.henry.universitycourseschedular.models.core.Department;

import java.util.List;

public interface DepartmentService {
    DefaultApiResponse<Department> createDepartment(DepartmentDto dto);
    DefaultApiResponse<Department> updateDepartment(Long id, DepartmentDto dto);
    DefaultApiResponse<?> deleteDepartment(Long id);
    DefaultApiResponse<List<Department>> getAllDepartments();
    DefaultApiResponse<Department> getDepartmentById(Long id);
}
