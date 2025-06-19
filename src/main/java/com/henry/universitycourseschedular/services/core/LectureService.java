package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models.Lecturer;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.LecturerRequestDto;

import java.util.List;

public interface LectureService {
    DefaultApiResponse<Lecturer> createLecturer(LecturerRequestDto dto);
    DefaultApiResponse<Lecturer> updateLecturer(Long id, LecturerRequestDto dto);
    DefaultApiResponse<?> deleteLecturer(Long id);
    DefaultApiResponse<List<Lecturer>> getAllLecturers();
    DefaultApiResponse<Lecturer> getLecturerById(Long id);
}
