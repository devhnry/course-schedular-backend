package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.VenueConstraint;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;

import java.util.List;

public interface VenueConstraintService {
    DefaultApiResponse<VenueConstraint> createConstraint(Long venueId, Long departmentId, boolean restricted);
    DefaultApiResponse<VenueConstraint> updateConstraint(Long constraintId, boolean restricted);
    DefaultApiResponse<?> deleteConstraint(Long constraintId);
    DefaultApiResponse<List<VenueConstraint>> getConstraintsByVenue(Long venueId);
    DefaultApiResponse<List<VenueConstraint>> getConstraintsByDepartment(Long departmentId);
    DefaultApiResponse<List<VenueConstraint>> getAllConstraints();

    // Validation
    boolean isVenueAllowedForProgram(Long venueId, String programCode);

    // Utility methods
    DefaultApiResponse<?> createBuildingBasedConstraints();
    DefaultApiResponse<?> createProgramSpecificConstraints();
}