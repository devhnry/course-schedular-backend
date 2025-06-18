package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.ProgramMapper;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.ProgramDto;
import com.henry.universitycourseschedular.models._dto.ProgramResponseDto;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.core.Program;
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

    @Override
    public DefaultApiResponse<ProgramResponseDto> createProgram(ProgramDto dto) {
        Department department = getDepartment(dto.departmentId());
        Program program = ProgramMapper.fromDto(dto, department);
        programRepo.save(program);
        Program saved = programRepo.save(program);
        return buildSuccessResponse("Program created", StatusCodes.ACTION_COMPLETED, ProgramMapper.toDto(saved));

    }

    @Override
    public DefaultApiResponse<ProgramResponseDto> updateProgram(Long id, ProgramDto dto) {
        Program program = programRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        Department department = getDepartment(dto.departmentId());
        ProgramMapper.updateFromDto(program, dto, department);
        Program updated = programRepo.save(program);
        return buildSuccessResponse("Program updated", StatusCodes.ACTION_COMPLETED, ProgramMapper.toDto(updated));

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
                .map(ProgramMapper::toDto)
                .toList();
        return buildSuccessResponse("All programs", StatusCodes.ACTION_COMPLETED, dtos);

    }

    @Override
    public DefaultApiResponse<ProgramResponseDto> getProgramById(Long id) {
        Program program = programRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        return buildSuccessResponse("Program found", StatusCodes.ACTION_COMPLETED, ProgramMapper.toDto(program));

    }

    private Department getDepartment(Long id) {
        return departmentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }
}