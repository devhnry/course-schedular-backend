package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.ProgramMapper;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Program;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.ProgramRequestDto;
import com.henry.universitycourseschedular.models._dto.ProgramResponseDto;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.ProgramRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @Slf4j
@AllArgsConstructor
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepo;
    private final DepartmentRepository departmentRepo;
    private final ProgramMapper programMapper;

    @Override
    public DefaultApiResponse<ProgramResponseDto> createProgram(ProgramRequestDto dto) {
        Department department = getDepartment(dto.departmentCode());
        Program program = programMapper.toEntity(dto);
        programRepo.save(program);
        Program saved = programRepo.save(program);
        return buildSuccessResponse("Program created", StatusCodes.ACTION_COMPLETED, programMapper.toDto(saved));

    }

    @Override
    public DefaultApiResponse<ProgramResponseDto> updateProgram(Long id, ProgramRequestDto dto) {
        Program program = programRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        Department department = getDepartment(dto.departmentCode());
        programMapper.updateFromDto(program, dto, department);
        Program updated = programRepo.save(program);
        return buildSuccessResponse("Program updated", StatusCodes.ACTION_COMPLETED,
                programMapper.toDto(updated));

    }

    @Override
    public DefaultApiResponse<?> deleteProgram(Long id) {
        if (!programRepo.existsById(id)) {
            return buildErrorResponse("Program not found");
        }
        programRepo.deleteById(id);
        return buildSuccessResponse("Program deleted");
    }

    @Override
    public DefaultApiResponse<List<ProgramResponseDto>> getAllPrograms() {
        List<ProgramResponseDto> dtos = programRepo.findAll().stream()
                .map(programMapper::toDto)
                .toList();
        return buildSuccessResponse("All programs", StatusCodes.ACTION_COMPLETED, dtos);
    }

    @Override
    public DefaultApiResponse<ProgramResponseDto> getProgramById(Long id) {
        Program program = programRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        return buildSuccessResponse("Program found", StatusCodes.ACTION_COMPLETED,
                programMapper.toDto(program));

    }

    private Department getDepartment(String code) {
        return departmentRepo.findByCode(code).orElseThrow(
                () -> new ResourceNotFoundException("Department not found"));
    }
}