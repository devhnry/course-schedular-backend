package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.dto.LecturerDto;
import com.henry.universitycourseschedular.models.Lecturer;

public class LecturerMapper {
    public static Lecturer fromCreateDto(LecturerDto dto) {
        return Lecturer.builder()
                .title(dto.getTitle())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();
    }

    public static void updateLecturerFromDto(Lecturer lecturer, LecturerDto dto) {
        lecturer.setTitle(dto.getTitle());
        lecturer.setFirstName(dto.getFirstName());
        lecturer.setLastName(dto.getLastName());
    }
}
