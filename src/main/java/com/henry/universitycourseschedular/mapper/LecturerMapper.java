package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.LecturerDto;
import com.henry.universitycourseschedular.models.core.Lecturer;

public class LecturerMapper {

    public static Lecturer fromCreateDto(LecturerDto dto) {
        return Lecturer.builder()
                .title(dto.title())
                .fullName(dto.fullName())
                .build();
    }

    public static void updateLecturerFromDto(Lecturer lecturer, LecturerDto dto) {
        lecturer.setFullName(dto.fullName());
        lecturer.setTitle(dto.title());
    }
}

