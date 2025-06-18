package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.CourseAssignmentMapper;
import com.henry.universitycourseschedular.models._dto.CourseAssignmentDto;
import com.henry.universitycourseschedular.models._dto.CourseAssignmentResponseDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.repositories.CourseAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseAssignmentServiceImpl implements CourseAssignmentService {

    private final CourseAssignmentRepository repository;
    private final CourseAssignmentMapper mapper;

    @Override
    public DefaultApiResponse<CourseAssignmentResponseDto> createAssignment(CourseAssignmentDto dto) {
        try {
            boolean exists = repository.existsByLecturerIdAndCourseId(dto.getLecturerId(), dto.getCourseId());
            if (exists) {
                return buildErrorResponse("Course Assignment already exists");
            }
            CourseAssignment assignment = mapper.fromDto(dto);

            CourseAssignment saved = repository.save(assignment);
            return buildSuccessResponse("Course assignment created", StatusCodes.ACTION_COMPLETED, mapper.toDto(saved));

        } catch (Exception e) {
            log.error("Error creating assignment", e);
            return buildErrorResponse("Failed to create assignment: " + e.getMessage());
        }
    }


    @Override
    public DefaultApiResponse<List<CourseAssignmentResponseDto>> getByDepartment(Long departmentId) {
        try {
            List<CourseAssignmentResponseDto> dtos = repository.findByDepartmentId(departmentId)
                    .stream().map(mapper::toDto).toList();
            return buildSuccessResponse("Assignments retrieved", StatusCodes.ACTION_COMPLETED, dtos);

        } catch (Exception e) {
            log.error("Error fetching assignments", e);
            return buildErrorResponse("Failed to retrieve assignments: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<List<CourseAssignmentResponseDto>> getByLecturer(Long lecturerId) {
        try {
            List<CourseAssignmentResponseDto> dtos = repository.findAllByLecturerId(lecturerId)
                    .stream().map(mapper::toDto).toList();
            return buildSuccessResponse("Assignments retrieved", StatusCodes.ACTION_COMPLETED, dtos);
        } catch (Exception e) {
            log.error("Error fetching assignments", e);
            return buildErrorResponse("Failed to retrieve assignments: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<CourseAssignmentResponseDto> updateAssignment(Long id, CourseAssignmentDto dto) {
        try {
            CourseAssignment existing = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
            mapper.updateEntityFromDto(existing, dto);
            repository.save(existing);

            CourseAssignment updated = repository.save(existing);
            return buildSuccessResponse("Assignment updated", StatusCodes.ACTION_COMPLETED, mapper.toDto(updated));
        } catch (Exception e) {
            log.error("Error updating assignment", e);
            return buildErrorResponse("Failed to update assignment: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<?> deleteAssignment(Long id) {
        try {
            if (!repository.existsById(id)) {
                throw new ResourceNotFoundException("CourseAssignment not found");
            }
            repository.deleteById(id);
            return buildSuccessResponse("Course assignment deleted");
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting assignment", e);
            return buildErrorResponse("Failed to delete assignment: " + e.getMessage());
        }
    }
}

