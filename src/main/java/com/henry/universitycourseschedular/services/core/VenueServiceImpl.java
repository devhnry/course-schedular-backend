import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.mapper.VenueMapper;
import com.henry.universitycourseschedular.models.CollegeBuilding;
import com.henry.universitycourseschedular.models.Venue;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.VenueRequestDto;
import com.henry.universitycourseschedular.models._dto.VenueResponseDto;
import com.henry.universitycourseschedular.repositories.CollegeBuildingRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import com.henry.universitycourseschedular.services.core.VenueService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service
@Slf4j
@AllArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepo;
    private final CollegeBuildingRepository buildingRepo;
    private final VenueMapper venueMapper;

    @Override
    public DefaultApiResponse<VenueResponseDto> createVenue(VenueRequestDto dto) {
        CollegeBuilding building = getBuilding(dto.collegeBuildingCode());
        Venue venue = venueMapper.toEntity(dto);
        venueRepo.save(venue);
        VenueResponseDto responseDto = venueMapper.toDto(venue);
        return buildSuccessResponse("Venue created", StatusCodes.ACTION_COMPLETED, responseDto);
    }

    @Override
    public DefaultApiResponse<VenueResponseDto> updateVenue(Long id, VenueRequestDto dto) {
        Venue venue = venueRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        CollegeBuilding building = getBuilding(dto.collegeBuildingCode());
        venueMapper.updateFromDto(venue, dto, building);
        venueRepo.save(venue);
        VenueResponseDto responseDto = venueMapper.toDto(venue);
        return buildSuccessResponse("Venue updated", StatusCodes.ACTION_COMPLETED, responseDto);
    }

    @Override
    public DefaultApiResponse<?> deleteVenue(Long id) {
        if (!venueRepo.existsById(id)) {
            return buildErrorResponse("Venue not found");
        }
        venueRepo.deleteById(id);
        return buildSuccessResponse("Venue deleted", StatusCodes.ACTION_COMPLETED, null);
    }

    @Override
    public DefaultApiResponse<List<VenueResponseDto>> getAllVenues() {
        List<Venue> venues = venueRepo.findAll();
        List<VenueResponseDto> responseDtos = venues.stream()
                .map(venueMapper::toDto)
                .toList();
        return buildSuccessResponse("All venues", StatusCodes.ACTION_COMPLETED, responseDtos);
    }

    @Override
    public DefaultApiResponse<VenueResponseDto> getVenueById(Long id) {
        Venue venue = venueRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        return buildSuccessResponse("Venue found", StatusCodes.ACTION_COMPLETED, venueMapper.toDto(venue));
    }

    private CollegeBuilding getBuilding(String code) {
        return buildingRepo.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("College building not found: " + code));
    }
}