package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.DepartmentMapper;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.DepartmentDto;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @RequiredArgsConstructor @Slf4j
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepository departmentRepo;
    private final CollegeBuildingRepository collegeBuildingRepo;

    @Override
    public DefaultApiResponse<Department> createDepartment(DepartmentDto dto) {
        try {
            CollegeBuilding building = getBuilding(dto.collegeBuildingId());
            Department department = DepartmentMapper.fromDto(dto, building);
            departmentRepo.save(department);
            return buildSuccessResponse("Department created", StatusCodes.ACTION_COMPLETED, department);
        } catch (Exception e) {
            log.error("Unable to create department", e);
            return buildErrorResponse("Error creating department");
        }
    }

    @Override
    public DefaultApiResponse<Department> updateDepartment(Long id, DepartmentDto dto) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        CollegeBuilding building = getBuilding(dto.collegeBuildingId());
        DepartmentMapper.updateFromDto(department, dto, building);
        departmentRepo.save(department);

        return buildSuccessResponse("Department updated", StatusCodes.ACTION_COMPLETED, department);
    }

    @Override
    public DefaultApiResponse<?> deleteDepartment(Long id) {
        if (!departmentRepo.existsById(id)) {
            return buildErrorResponse("Department not found");
        }
        departmentRepo.deleteById(id);
        return buildSuccessResponse("Department deleted");
    }

    @Override
    public DefaultApiResponse<List<Department>> getAllDepartments() {
        return buildSuccessResponse("All departments", StatusCodes.ACTION_COMPLETED, departmentRepo.findAll());
    }

    @Override
    public DefaultApiResponse<Department> getDepartmentById(Long id) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return buildSuccessResponse("Department found", StatusCodes.ACTION_COMPLETED, department);
    }

    private CollegeBuilding getBuilding(Long id) {
        return collegeBuildingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("College building not found"));
    }
}
