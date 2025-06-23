package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.LecturerRequestDto;
import com.henry.universitycourseschedular.models._dto.LecturerResponseDto;

import java.util.List;

public interface LectureService {
    DefaultApiResponse<LecturerResponseDto> createLecturer(LecturerRequestDto dto);
    DefaultApiResponse<LecturerResponseDto> updateLecturer(Long id, LecturerRequestDto dto);
    DefaultApiResponse<?> deleteLecturer(Long id);
    DefaultApiResponse<List<LecturerResponseDto>> getAllLecturers();
    DefaultApiResponse<LecturerResponseDto> getLecturerById(Long id);
}
