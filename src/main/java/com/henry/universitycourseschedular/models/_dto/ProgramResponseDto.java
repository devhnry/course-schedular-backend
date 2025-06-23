package com.henry.universitycourseschedular.models._dto;

public record ProgramResponseDto(
        Long id,
        String name,
        String departmentCode,
        String departmentName,
        String collegeName
) {}
