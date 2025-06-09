package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.VenueDto;
import com.henry.universitycourseschedular.models.core.Venue;

import java.util.List;

public interface VenueService {
    DefaultApiResponse<Venue> createVenue(VenueDto dto);
    DefaultApiResponse<Venue> updateVenue(Long id, VenueDto dto);
    DefaultApiResponse<?> deleteVenue(Long id);
    DefaultApiResponse<List<Venue>> getAllVenues();
    DefaultApiResponse<Venue> getVenueById(Long id);
}
