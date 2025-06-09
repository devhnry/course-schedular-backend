package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.GeneralBodyMapper;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.GeneralBodyDto;
import com.henry.universitycourseschedular.models.core.GeneralBody;
import com.henry.universitycourseschedular.repositories.GeneralBodyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @RequiredArgsConstructor @Slf4j
public class GeneralBodyServiceImpl implements GeneralBodyService {

    private final GeneralBodyRepository generalBodyRepo;

    @Override
    public DefaultApiResponse<GeneralBody> createGeneralBody(GeneralBodyDto dto) {
        try {
            GeneralBody body = GeneralBodyMapper.fromDto(dto);
            generalBodyRepo.save(body);
            return buildSuccessResponse("General body created", StatusCodes.ACTION_COMPLETED, body);
        } catch (Exception e) {
            log.error("Unable to create general body", e);
            return buildErrorResponse("Error creating general body");
        }
    }

    @Override
    public DefaultApiResponse<GeneralBody> updateGeneralBody(Long id, GeneralBodyDto dto) {
        GeneralBody existing = generalBodyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General body not found"));

        GeneralBodyMapper.updateFromDto(existing, dto);
        generalBodyRepo.save(existing);

        return buildSuccessResponse("General body updated", StatusCodes.ACTION_COMPLETED, existing);
    }

    @Override
    public DefaultApiResponse<?> deleteGeneralBody(Long id) {
        if (!generalBodyRepo.existsById(id)) {
            return buildErrorResponse("General body not found");
        }
        generalBodyRepo.deleteById(id);
        return buildSuccessResponse("General body deleted");
    }

    @Override
    public DefaultApiResponse<List<GeneralBody>> getAllGeneralBodies() {
        return buildSuccessResponse("All general bodies", StatusCodes.ACTION_COMPLETED, generalBodyRepo.findAll());
    }

    @Override
    public DefaultApiResponse<GeneralBody> getGeneralBodyById(Long id) {
        GeneralBody body = generalBodyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General body not found"));
        return buildSuccessResponse("General body found", StatusCodes.ACTION_COMPLETED, body);
    }
}