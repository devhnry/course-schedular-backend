package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models.Venue;
import com.henry.universitycourseschedular.models.VenueConstraint;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.VenueConstraintRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildErrorResponse;
import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class VenueConstraintServiceImpl implements VenueConstraintService {

    // Architecture program specific venues in CST building
    private static final List<String> ARCHITECTURE_ALLOWED_VENUES = List.of(
            "CST 401", "CST 402", "CST 403", "CST 404", "CST 405",
            "CST 406", "CST 407", "CST 408", "CST 409", "CST 410",
            "ARCHITECTURE STUDIO 1", "ARCHITECTURE STUDIO 2",
            "ARCHITECTURE STUDIO 3", "DESIGN STUDIO"
    );
    private final VenueConstraintRepository constraintRepository;
    private final VenueRepository venueRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public DefaultApiResponse<VenueConstraint> createConstraint(Long venueId, Long departmentId, boolean restricted) {
        try {
            Venue venue = venueRepository.findById(venueId)
                    .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

            // Check if constraint already exists
            VenueConstraint existing = constraintRepository.findByVenueIdAndDepartmentId(venueId, departmentId);
            if (existing != null) {
                return buildErrorResponse("Constraint already exists for this venue-department combination");
            }

            VenueConstraint constraint = VenueConstraint.builder()
                    .venue(venue)
                    .preferredDepartment(department)
                    .restricted(restricted)
                    .build();

            constraintRepository.save(constraint);
            return buildSuccessResponse("Venue constraint created", StatusCodes.ACTION_COMPLETED, constraint);
        } catch (Exception e) {
            log.error("Error creating venue constraint", e);
            return buildErrorResponse("Failed to create constraint: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<VenueConstraint> updateConstraint(Long constraintId, boolean restricted) {
        try {
            VenueConstraint constraint = constraintRepository.findById(constraintId)
                    .orElseThrow(() -> new ResourceNotFoundException("Constraint not found"));

            constraint.setRestricted(restricted);
            constraintRepository.save(constraint);

            return buildSuccessResponse("Constraint updated", StatusCodes.ACTION_COMPLETED, constraint);
        } catch (Exception e) {
            log.error("Error updating constraint", e);
            return buildErrorResponse("Failed to update constraint: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<?> deleteConstraint(Long constraintId) {
        try {
            if (!constraintRepository.existsById(constraintId)) {
                throw new ResourceNotFoundException("Constraint not found");
            }
            constraintRepository.deleteById(constraintId);
            return buildSuccessResponse("Constraint deleted");
        } catch (Exception e) {
            log.error("Error deleting constraint", e);
            return buildErrorResponse("Failed to delete constraint: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<List<VenueConstraint>> getConstraintsByVenue(Long venueId) {
        try {
            List<VenueConstraint> constraints = constraintRepository.findByVenueId(venueId);
            return buildSuccessResponse("Constraints retrieved", StatusCodes.ACTION_COMPLETED, constraints);
        } catch (Exception e) {
            log.error("Error retrieving constraints by venue", e);
            return buildErrorResponse("Failed to retrieve constraints: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<List<VenueConstraint>> getConstraintsByDepartment(Long departmentId) {
        try {
            List<VenueConstraint> constraints = constraintRepository.findByPreferredDepartmentId(departmentId);
            return buildSuccessResponse("Constraints retrieved", StatusCodes.ACTION_COMPLETED, constraints);
        } catch (Exception e) {
            log.error("Error retrieving constraints by department", e);
            return buildErrorResponse("Failed to retrieve constraints: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<List<VenueConstraint>> getAllConstraints() {
        try {
            List<VenueConstraint> constraints = constraintRepository.findAll();
            return buildSuccessResponse("All constraints retrieved", StatusCodes.ACTION_COMPLETED, constraints);
        } catch (Exception e) {
            log.error("Error retrieving all constraints", e);
            return buildErrorResponse("Failed to retrieve constraints: " + e.getMessage());
        }
    }

//    @Override
//    public boolean isVenueAllowedForProgram(Long venueId, String programName) {
//        return false;
//    }

    @Override
    public DefaultApiResponse<?> createBuildingBasedConstraints() {
        try {
            log.info("Creating building-based venue constraints...");

            List<Department> allDepartments = departmentRepository.findAll();
            List<Venue> allVenues = venueRepository.findAll();
            List<VenueConstraint> constraintsToSave = new ArrayList<>();

            for (Venue venue : allVenues) {
                if (venue.getCollegeBuilding() == null) {
                    log.debug("Skipping venue {} - no building assigned", venue.getName());
                    continue;
                }

                for (Department department : allDepartments) {
                    if (department.getCollegeBuilding() == null) {
                        log.debug("Skipping department {} - no building assigned", department.getName());
                        continue;
                    }

                    // Check if constraint already exists
                    VenueConstraint existing = constraintRepository.findByVenueIdAndDepartmentId(
                            venue.getId(), department.getId());
                    if (existing != null) {
                        continue;
                    }

                    // Create restriction if buildings don't match
                    boolean isRestricted = !venue.getCollegeBuilding().getId()
                            .equals(department.getCollegeBuilding().getId());

                    if (isRestricted) {
                        VenueConstraint constraint = VenueConstraint.builder()
                                .venue(venue)
                                .preferredDepartment(department)
                                .restricted(true)
                                .build();
                        constraintsToSave.add(constraint);
                    }
                }
            }

            constraintRepository.saveAll(constraintsToSave);
            log.info("Created {} building-based constraints", constraintsToSave.size());

            return buildSuccessResponse("Building-based constraints created",
                    StatusCodes.ACTION_COMPLETED, constraintsToSave.size());
        } catch (Exception e) {
            log.error("Error creating building-based constraints", e);
            return buildErrorResponse("Failed to create building-based constraints: " + e.getMessage());
        }
    }

    @Override
    public DefaultApiResponse<?> createProgramSpecificConstraints() {
        try {
            log.info("Creating program-specific venue constraints...");

            List<Venue> allVenues = venueRepository.findAll();
            List<Department> allDepartments = departmentRepository.findAll();
            List<VenueConstraint> constraintsToSave = new ArrayList<>();

            // Architecture program constraints - only 400 level CST venues
            Department archDept = allDepartments.stream()
                    .filter(d -> "ARCH".equals(d.getCode()))
                    .findFirst()
                    .orElse(null);

            if (archDept != null) {
                for (Venue venue : allVenues) {
                    // Check if constraint already exists
                    VenueConstraint existing = constraintRepository.findByVenueIdAndDepartmentId(
                            venue.getId(), archDept.getId());
                    if (existing != null) {
                        continue;
                    }

                    String venueName = venue.getName().toUpperCase();
                    boolean isArchAllowed = venueName.contains("CST") && venueName.contains("4");

                    if (!isArchAllowed && venue.getCollegeBuilding() != null &&
                            venue.getCollegeBuilding().getCode().equals("CST")) {
                        VenueConstraint constraint = VenueConstraint.builder()
                                .venue(venue)
                                .preferredDepartment(archDept)
                                .restricted(true)
                                .build();
                        constraintsToSave.add(constraint);
                    }
                }
            }

            // Add more program-specific constraints here as needed

            constraintRepository.saveAll(constraintsToSave);
            log.info("Created {} program-specific constraints", constraintsToSave.size());

            return buildSuccessResponse("Program-specific constraints created",
                    StatusCodes.ACTION_COMPLETED, constraintsToSave.size());
        } catch (Exception e) {
            log.error("Error creating program-specific constraints", e);
            return buildErrorResponse("Failed to create program-specific constraints: " + e.getMessage());
        }
    }

    private boolean isArchitectureVenueAllowed(String venueName) {
        String upperVenueName = venueName.toUpperCase();
        return ARCHITECTURE_ALLOWED_VENUES.stream()
                .anyMatch(allowedVenue -> upperVenueName.contains(allowedVenue.toUpperCase()));
    }

    @Override
    public boolean isVenueAllowedForProgram(Long venueId, String programCode) {
        if ("ARCH".equals(programCode)) {
            Venue venue = venueRepository.findById(venueId).orElse(null);
            if (venue == null) return false;
            return isArchitectureVenueAllowed(venue.getName());
        }

        // Add other program-specific logic here
        return true; // Default allow for other programs
    }
}