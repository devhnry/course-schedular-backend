package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.LecturerMapper;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.LecturerDto;
import com.henry.universitycourseschedular.models.core.Lecturer;
import com.henry.universitycourseschedular.repositories.LecturerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @Slf4j
@AllArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final LecturerRepository lecturerRepository;

    @Override
    public DefaultApiResponse<Lecturer> createLecturer(LecturerDto dto) {
        try {
            Lecturer lecturer = LecturerMapper.fromCreateDto(dto);
            lecturerRepository.save(lecturer);
            return buildSuccessResponse("Lecturer Added", StatusCodes.ACTION_COMPLETED, lecturer);
        } catch (Exception e) {
            log.error("Unable to create lecturer", e);
            return buildErrorResponse("An Error Occurred while adding LECTURER");
        }
    }

    @Override
    public DefaultApiResponse<List<Lecturer>> getAllLecturers() {
        try {
            return buildSuccessResponse("All Lecturers Listed", StatusCodes.ACTION_COMPLETED, lecturerRepository.findAll());
        } catch (Exception e) {
            return buildErrorResponse(String.format("An Error Occurred %s", e.getMessage()));
        }
    }

    @Override
    public DefaultApiResponse<Lecturer> getLecturerById(Long id) {
        try {
            Lecturer lecturer = lecturerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                    "Lecturer not found"));
            return buildSuccessResponse("Lecturer Found", StatusCodes.ACTION_COMPLETED, lecturer);
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<Lecturer> updateLecturer(Long id, LecturerDto dto) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));

        LecturerMapper.updateLecturerFromDto(lecturer, dto);
        lecturerRepository.save(lecturer);
        return buildSuccessResponse("Lecturer Updated Successfully", StatusCodes.ACTION_COMPLETED, lecturer);
    }

    @Override
    public DefaultApiResponse<?> deleteLecturer(Long id) {
        try {
            if (!lecturerRepository.existsById(id)) {
                throw new ResourceNotFoundException("Lecturer not found");
            }
            lecturerRepository.deleteById(id);
            return buildSuccessResponse("Lecturer Deleted Successfully");
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e.getMessage());
        }
    }
}
