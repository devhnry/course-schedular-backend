package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.CollegeBuildingMapper;
import com.henry.universitycourseschedular.models._dto.CollegeBuildingDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @Slf4j
@AllArgsConstructor
public class CollegeBuildingServiceImpl implements CollegeBuildingService {

    private final CollegeBuildingRepository repo;

    @Override
    public DefaultApiResponse<CollegeBuilding> create(CollegeBuildingDto dto) {
        CollegeBuilding building = CollegeBuildingMapper.fromDto(dto);
        repo.save(building);
        return buildSuccessResponse("College building created", StatusCodes.ACTION_COMPLETED, building);
    }

    @Override
    public DefaultApiResponse<CollegeBuilding> update(Long id, CollegeBuildingDto dto) {
        CollegeBuilding cb = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Building not found"));
        CollegeBuildingMapper.updateFromDto(cb, dto);
        repo.save(cb);
        return buildSuccessResponse("College building updated", StatusCodes.ACTION_COMPLETED, cb);
    }

    @Override
    public DefaultApiResponse<?> delete(Long id) {
        if (!repo.existsById(id)) {
            return buildErrorResponse("College building not found");
        }
        repo.deleteById(id);
        return buildSuccessResponse("College building deleted");
    }

    @Override
    public DefaultApiResponse<List<CollegeBuilding>> getAll() {
        return buildSuccessResponse("All college buildings", StatusCodes.ACTION_COMPLETED, repo.findAll());
    }

    @Override
    public DefaultApiResponse<CollegeBuilding> getById(Long id) {
        CollegeBuilding cb = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Building not found"));
        return buildSuccessResponse("College building found", StatusCodes.ACTION_COMPLETED, cb);
    }
}
