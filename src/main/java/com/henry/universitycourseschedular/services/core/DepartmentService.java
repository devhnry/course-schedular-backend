package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.DepartmentRequestDto;
import com.henry.universitycourseschedular.models._dto.DepartmentResponseDto;

import java.util.List;

public interface DepartmentService {
    DefaultApiResponse<DepartmentResponseDto> createDepartment(DepartmentRequestDto dto);
    DefaultApiResponse<DepartmentResponseDto> updateDepartment(Long id, DepartmentRequestDto dto);
    DefaultApiResponse<?> deleteDepartment(Long id);
    DefaultApiResponse<List<DepartmentResponseDto>> getAllDepartments();
    DefaultApiResponse<DepartmentResponseDto> getDepartmentById(Long id);
}
