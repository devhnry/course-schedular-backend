package com.henry.universitycourseschedular.models._dto;

public record DepartmentRequestDto(
        String name,
        String code,
        String collegeBuildingCode
) {}