package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.VenueMapper;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.VenueDto;
import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @Slf4j
@AllArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepo;
    private final CollegeBuildingRepository buildingRepo;

    @Override
    public DefaultApiResponse<Venue> createVenue(VenueDto dto) {
        CollegeBuilding building = getBuilding(dto.collegeBuildingId());
        Venue venue = VenueMapper.fromDto(dto, building);
        venueRepo.save(venue);
        return buildSuccessResponse("Venue created", StatusCodes.ACTION_COMPLETED, venue);
    }

    @Override
    public DefaultApiResponse<Venue> updateVenue(Long id, VenueDto dto) {
        Venue venue = venueRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        CollegeBuilding building = getBuilding(dto.collegeBuildingId());
        VenueMapper.updateFromDto(venue, dto, building);
        venueRepo.save(venue);
        return buildSuccessResponse("Venue updated", StatusCodes.ACTION_COMPLETED, venue);
    }

    @Override
    public DefaultApiResponse<?> deleteVenue(Long id) {
        if (!venueRepo.existsById(id)) {
            return buildErrorResponse("Venue not found");
        }
        venueRepo.deleteById(id);
        return buildSuccessResponse("Venue deleted");
    }

    @Override
    public DefaultApiResponse<List<Venue>> getAllVenues() {
        return buildSuccessResponse("All venues", StatusCodes.ACTION_COMPLETED, venueRepo.findAll());
    }

    @Override
    public DefaultApiResponse<Venue> getVenueById(Long id) {
        Venue venue = venueRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        return buildSuccessResponse("Venue found", StatusCodes.ACTION_COMPLETED, venue);
    }

    private CollegeBuilding getBuilding(Long id) {
        return buildingRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("College building not found"));
    }
}
