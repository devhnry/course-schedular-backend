package com.henry.universitycourseschedular.models._dto;

public record DepartmentResponseDto(
        Long id,
        String name,
        String code,
        String collegeBuildingCode,
        String collegeBuildingName
) {}
