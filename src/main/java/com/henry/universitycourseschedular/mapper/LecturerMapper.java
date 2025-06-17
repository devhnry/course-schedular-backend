package com.henry.universitycourseschedular.mapper;

import com.henry.universitycourseschedular.models._dto.LecturerDto;
import com.henry.universitycourseschedular.models.core.Lecturer;

public class LecturerMapper {

    public static Lecturer fromCreateDto(LecturerDto dto) {
        return Lecturer.builder()
                .title(dto.title())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
//                .email(dto.email())
                .build();
    }

    public static void updateLecturerFromDto(Lecturer lecturer, LecturerDto dto) {
        lecturer.setFirstName(dto.firstName());
        lecturer.setLastName(dto.lastName());
        lecturer.setTitle(dto.title());
//        lecturer.setEmail(dto.email());
    }
}

