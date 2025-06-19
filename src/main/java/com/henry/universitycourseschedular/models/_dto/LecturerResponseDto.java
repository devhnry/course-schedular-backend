package com.henry.universitycourseschedular.models._dto;

public record LecturerResponseDto(
        Long id,
        String fullName,
        String departmentCode,
        String departmentName
) {}

