package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.LecturerDto;
import com.henry.universitycourseschedular.models.core.Lecturer;

import java.util.List;

public interface LectureService {
    DefaultApiResponse<Lecturer> createLecturer(LecturerDto dto);
    DefaultApiResponse<Lecturer> updateLecturer(Long id, LecturerDto dto);
    DefaultApiResponse<?> deleteLecturer(Long id);
    DefaultApiResponse<List<Lecturer>> getAllLecturers();
    DefaultApiResponse<Lecturer> getLecturerById(Long id);
}
