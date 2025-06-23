package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.VenueRequestDto;
import com.henry.universitycourseschedular.models._dto.VenueResponseDto;

import java.util.List;

public interface VenueService {
    DefaultApiResponse<VenueResponseDto> createVenue(VenueRequestDto dto);
    DefaultApiResponse<VenueResponseDto> updateVenue(Long id, VenueRequestDto dto);
    DefaultApiResponse<?> deleteVenue(Long id);
    DefaultApiResponse<List<VenueResponseDto>> getAllVenues();
    DefaultApiResponse<VenueResponseDto> getVenueById(Long id);
}
