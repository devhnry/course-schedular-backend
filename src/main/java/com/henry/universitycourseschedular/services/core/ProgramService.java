package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.ProgramDto;
import com.henry.universitycourseschedular.models.core.Program;

import java.util.List;

public interface ProgramService {
    DefaultApiResponse<Program> createProgram(ProgramDto dto);
    DefaultApiResponse<Program> updateProgram(Long id, ProgramDto dto);
    DefaultApiResponse<?> deleteProgram(Long id);
    DefaultApiResponse<List<Program>> getAllPrograms();
    DefaultApiResponse<Program> getProgramById(Long id);
}
