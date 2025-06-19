package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.CourseAssignmentMapper;
import com.henry.universitycourseschedular.models.Course;
import com.henry.universitycourseschedular.models.CourseAssignment;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Lecturer;
import com.henry.universitycourseschedular.models._dto.CourseAssignmentRequestDto;
import com.henry.universitycourseschedular.models._dto.CourseAssignmentResponseDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.repositories.CourseAssignmentRepository;
import com.henry.universitycourseschedular.repositories.CourseRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.LecturerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseAssignmentServiceImpl implements CourseAssignmentService {

    private final CourseAssignmentRepository repository;
    private final CourseAssignmentMapper mapper;
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public DefaultApiResponse<CourseAssignmentResponseDto> createAssignment(CourseAssignmentRequestDto dto) {
        try {
            Course course = courseRepository.findByCode(dto.courseCode()).orElseThrow(
                    () -> new ResourceNotFoundException("Course not found with code " + dto.courseCode())
            );
            Set<Lecturer> lecturers = dto.lecturerNames().stream()
                    .map(name -> findOrCreateLecturer(name, course.getProgram().getDepartment().getCode()))
                    .collect(Collectors.toSet());

            CourseAssignment assignment = mapper.fromDto(dto, lecturers);
            CourseAssignment saved = repository.save(assignment);

            return buildSuccessResponse("Course assignment created", StatusCodes.ACTION_COMPLETED, mapper.toDto(saved));
        } catch (Exception e) {
            log.error("Error creating assignment", e);
            return buildErrorResponse("Failed to create assignment: " + e.getMessage());
        }
    }

    private Lecturer findOrCreateLecturer(String name, String departmentCode) {
        return lecturerRepository.findByFullNameAndDepartment_Code(name, departmentCode)
                .orElseGet(() -> {
                    Department dept = departmentRepository.findByCode(departmentCode)
                            .orElseThrow(() -> new RuntimeException("Department not found: " + departmentCode));
                    Lecturer newLecturer = Lecturer.builder()
                            .fullName(name)
                            .department(dept)
                            .build();
                    return lecturerRepository.save(newLecturer);
                });
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

//    @Override
//    public DefaultApiResponse<List<CourseAssignmentResponseDto>> getByLecturer(Long lecturerId) {
//        try {
//            List<CourseAssignmentResponseDto> dtos = repository.findAllByLecturerId(lecturerId)
//                    .stream().map(mapper::toDto).toList();
//            return buildSuccessResponse("Assignments retrieved", StatusCodes.ACTION_COMPLETED, dtos);
//        } catch (Exception e) {
//            log.error("Error fetching assignments", e);
//            return buildErrorResponse("Failed to retrieve assignments: " + e.getMessage());
//        }
//    }

    @Override
    public DefaultApiResponse<CourseAssignmentResponseDto> updateAssignment(Long id, CourseAssignmentRequestDto dto) {
        try {
            CourseAssignment existing = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
            Course course = courseRepository.findByCode(dto.courseCode()).orElseThrow(
                    () -> new ResourceNotFoundException("Course not found with code " + dto.courseCode())
            );
            String deptCode = course.getProgram().getDepartment().getCode();
            Set<Lecturer> lecturers = dto.lecturerNames().stream()
                    .map(name -> findOrCreateLecturer(name, deptCode))
                    .collect(Collectors.toSet());

            mapper.updateEntityFromDto(existing, dto, lecturers);
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

