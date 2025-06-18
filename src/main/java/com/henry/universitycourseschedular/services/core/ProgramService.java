package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.ProgramDto;
import com.henry.universitycourseschedular.models._dto.ProgramResponseDto;

import java.util.List;

public interface ProgramService {
    DefaultApiResponse<ProgramResponseDto> createProgram(ProgramDto dto);
    DefaultApiResponse<ProgramResponseDto> updateProgram(Long id, ProgramDto dto);
    DefaultApiResponse<?> deleteProgram(Long id);
    DefaultApiResponse<List<ProgramResponseDto>> getAllPrograms();
    DefaultApiResponse<ProgramResponseDto> getProgramById(Long id);
}
