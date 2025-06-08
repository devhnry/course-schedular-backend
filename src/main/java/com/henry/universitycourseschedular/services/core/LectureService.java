package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.LecturerDto;
import com.henry.universitycourseschedular.models.core.Lecturer;

import java.util.List;

public interface LectureService {
    DefaultApiResponse<Lecturer> createLecturer(LecturerDto dto);
    DefaultApiResponse<Lecturer> updateLecturer(String id, LecturerDto dto);
    DefaultApiResponse<?> deleteLecturer(String id);
    DefaultApiResponse<List<Lecturer>> getAllLecturers();
    DefaultApiResponse<Lecturer> getLecturerById(String id);
}
