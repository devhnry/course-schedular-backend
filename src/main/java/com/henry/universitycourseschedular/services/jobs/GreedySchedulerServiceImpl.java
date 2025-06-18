package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import com.henry.universitycourseschedular.models.schedule.TimeSlot;
import com.henry.universitycourseschedular.models.schedule.VenueConstraint;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.VenueConstraintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GreedySchedulerServiceImpl implements GreedySchedulerService {

    private static final List<DayOfWeek> DLD_ALLOWED_DAYS = List.of(
            DayOfWeek.TUESDAY,
            DayOfWeek.THURSDAY
    );
    private static final LocalTime DLD_ALLOWED_TIME = LocalTime.of(12, 0); // 12:00 PM

    // Direct venue mapping for general courses (no proxy departments needed)
    private static final Map<String, List<String>> GENERAL_COURSE_TO_VENUES = Map.of(
            "TMC", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "DLD", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "ALDC", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "GST", List.of("LECTURE THEATRE 1", "LECTURE THEATRE 2"),
            "EDS", List.of("LECTURE THEATRE 1", "LECTURE THEATRE 2"),
            "CEDS", List.of("LECTURE THEATRE 1", "LECTURE THEATRE 2")
    );

    private final VenueConstraintRepository venueConstraintRepository;
    private final DepartmentRepository departmentRepository;
    private final VenueConstraintService venueConstraintService;

    @Override
    public List<ScheduleEntry> assignCourses(List<CourseAssignment> assignments, List<TimeSlot> slots, List<Venue> venues) {
        log.info("Starting course assignment...");
        log.debug("Assignments: {}", assignments.size());
        log.debug("TimeSlots: {}", slots.size());
        log.debug("Venues: {}", venues.size());

        List<ScheduleEntry> scheduledEntries = new ArrayList<>();
        Map<Long, Set<Long>> lecturerBusySlots = new HashMap<>();
        Map<Long, Set<Long>> venueBusySlots = new HashMap<>();
        Map<LocalDate, Set<String>> dailyGeneralCourseMap = new HashMap<>();

        Map<String, Boolean> restrictionMap;
        try {
            restrictionMap = preloadVenueConstraints(venues);
        } catch (Exception e) {
            log.error("Error loading venue constraints", e);
            throw e;
        }

        // Create a mutable copy of the assignments list before sorting
        List<CourseAssignment> mutableAssignments = new ArrayList<>(assignments);

        log.debug("Sorting assignments...");
        try {
            mutableAssignments.sort(Comparator
                    .comparing((CourseAssignment a) -> {
                        return !a.isGeneral();
                    })
                    .thenComparingInt(a -> {
                        int count = availableOptionsCount(a, slots, venues, restrictionMap);
                        log.debug("Course {} has {} available options", a.getCourse().getCourseCode(), count);
                        return count;
                    }));
        } catch (Exception e){
            log.error("Error sorting assignments", e);
        }

        // Use the mutable copy for the rest of the method
        for (CourseAssignment assignment : mutableAssignments) {
            log.debug("Attempting to assign course: {}", assignment.getCourse().getCourseCode());
            boolean assigned = false;

            // Special logging for DLD
            boolean isDLD = assignment.getCourse().getCourseCode().toUpperCase().contains("DLD");
            if (isDLD) {
                log.info("🔍 Processing DLD course: {}", assignment.getCourse().getCourseCode());
                log.info("🔍 DLD is general course: {}", assignment.isGeneral());
            }

            for (TimeSlot slot : slots) {
                if (isDLD) {
                    DayOfWeek slotDay = slot.getDayOfWeek();
//                    log.info("🔍 DLD checking slot {}: {} {} - {}",
//                            slot.getId(),
//                            slotDay,
//                            slot.getStartTime().toLocalTime(),
//                            slot.getEndTime().toLocalTime());
                }

                if (!canAssignGeneralCourse(assignment, slot, dailyGeneralCourseMap)) {
//                    if (isDLD) {
//                        log.info("🔍 DLD failed general course check for slot {}", slot.getId());
//                    }
                    continue;
                }

                String deptName = assignment.getDepartment() != null ? assignment.getDepartment().getName() : "";
                if (!canAssignDuringChapel(slot, deptName)) {
//                    if (isDLD) {
//                        log.info("🔍 DLD failed chapel time check for slot {}", slot.getId());
//                    }
                    continue;
                }

                Set<Long> lecturerSlots = lecturerBusySlots.getOrDefault(assignment.getLecturer().getId(), new HashSet<>());
                if (lecturerSlots.contains(slot.getId())) {
//                    if (isDLD) {
//                        log.info("🔍 DLD lecturer {} is busy at slot {}", assignment.getLecturer().getId(), slot.getId());
//                    }
                    continue;
                }

                Optional<Venue> freeVenue = venues.stream()
                        .filter(v -> isVenueSuitableForAssignment(v, assignment, restrictionMap))
                        .filter(v -> !venueBusySlots.getOrDefault(v.getId(), new HashSet<>()).contains(slot.getId()))
                        .findFirst();

                if (freeVenue.isPresent()) {
                    Venue venue = freeVenue.get();
                    log.info("Assigning course {} to slot {} and venue {}", assignment.getCourse().getCourseCode(), slot.getId(), venue.getName());
                    registerAssignment(scheduledEntries, assignment, slot, venue, lecturerBusySlots, venueBusySlots);
                    trackGeneralCourse(assignment, slot, dailyGeneralCourseMap);
                    assigned = true;
                    break;
                } else if (isDLD) {
//                    log.info("🔍 DLD no suitable venue found for slot {}", slot.getId());
                    long suitableVenues = venues.stream()
                            .filter(v -> isVenueSuitableForAssignment(v, assignment, restrictionMap))
                            .count();
                    long availableVenues = venues.stream()
                            .filter(v -> !venueBusySlots.getOrDefault(v.getId(), new HashSet<>()).contains(slot.getId()))
                            .count();
//                    log.info("🔍 DLD suitable venues: {}, available venues: {}, total venues: {}",
//                            suitableVenues, availableVenues, venues.size());
                }
            }

            if (!assigned) {
                log.warn("Could not assign course {}", assignment.getCourse().getCourseCode());
                if (isDLD) {
                    log.error("🔍 DLD ASSIGNMENT FAILED - Check time slots and venue constraints");
                }
            }
        }

        log.info("Course assignment complete. Total scheduled entries: {}", scheduledEntries.size());
        return scheduledEntries;
    }

    private void registerAssignment(List<ScheduleEntry> scheduledEntries, CourseAssignment assignment, TimeSlot slot, Venue venue,
                                    Map<Long, Set<Long>> lecturerBusySlots, Map<Long, Set<Long>> venueBusySlots) {
        log.debug("Registering schedule entry for course: {}, slot: {}, venue: {}", assignment.getCourse().getCourseCode(), slot.getId(), venue.getId());

        ScheduleEntry entry = new ScheduleEntry();
        entry.setCourseAssignment(assignment);
        entry.setTimeSlot(slot);
        entry.setVenue(venue);
        scheduledEntries.add(entry);

        lecturerBusySlots.computeIfAbsent(assignment.getLecturer().getId(), k -> new HashSet<>()).add(slot.getId());
        venueBusySlots.computeIfAbsent(venue.getId(), k -> new HashSet<>()).add(slot.getId());
    }

    private boolean isVenueSuitableForAssignment(Venue venue, CourseAssignment assignment, Map<String, Boolean> restrictionMap) {
        if (venue == null || assignment == null) {
            log.warn("Null venue or assignment in suitability check");
            return false;
        }

        // Handle general courses - STRICT venue matching
        if (assignment.isGeneral()) {
            String generalBodyCode = assignment.getCourse().getGeneralBody() != null ?
                    assignment.getCourse().getGeneralBody().getCode() : null;

            if (generalBodyCode != null) {
                List<String> allowedVenues = GENERAL_COURSE_TO_VENUES.get(generalBodyCode);
                if (allowedVenues != null) {
                    String venueName = venue.getName().toUpperCase();
                    boolean isAllowed = allowedVenues.stream()
                            .anyMatch(allowedVenue -> venueName.contains(allowedVenue.toUpperCase()));

                    log.debug("General course {} checking venue {}: allowed={}",
                            generalBodyCode, venue.getName(), isAllowed);
                    return isAllowed;
                } else {
                    log.debug("No venue mapping for general course: {}", generalBodyCode);
                    return false; // Changed from fallback to strict rejection
                }
            }
            return false; // No general body means no venue assignment
        }

        // Handle regular department courses - building-based restrictions + program-specific constraints
        if (assignment.getDepartment() == null) {
            log.warn("Regular course assignment has no department: {}", assignment.getCourse().getCourseCode());
            return false;
        }

        // If venue has no building, it's a general venue (like CHAPEL, LT1, LT2)
        // Regular courses should not use these unless specifically allowed
        if (venue.getCollegeBuilding() == null) {
            log.debug("Regular course {} cannot use general venue {}",
                    assignment.getCourse().getCourseCode(), venue.getName());
            return false;
        }

        // Check program-specific venue constraints first
        if (assignment.getProgram() != null) {
            String programCode = assignment.getProgram().getCode();
            if (!isProgramAllowedInVenue(programCode, venue)) {
                log.debug("Program {} not allowed in venue {}", programCode, venue.getName());
                return false;
            }
        }

        // Check building-based restrictions using venue constraints
        String key = venue.getId() + "-" + assignment.getDepartment().getId();
        boolean isRestricted = restrictionMap.getOrDefault(key, false);

        // If no explicit constraint exists, check if department building matches venue building
        if (!restrictionMap.containsKey(key)) {
            if (assignment.getDepartment().getCollegeBuilding() != null && venue.getCollegeBuilding() != null) {
                boolean buildingMatch = assignment.getDepartment().getCollegeBuilding().getId()
                        .equals(venue.getCollegeBuilding().getId());
                log.debug("No explicit constraint for venue {}, department {}. Building match: {}",
                        venue.getName(), assignment.getDepartment().getName(), buildingMatch);
                return buildingMatch;
            }
        }

        return !isRestricted;
    }

    private boolean isProgramAllowedInVenue(String programCode, Venue venue) {
        // Use the service to check program-specific constraints
        return venueConstraintService.isVenueAllowedForProgram(venue.getId(), programCode);
    }

    private boolean canAssignDuringChapel(TimeSlot slot, String department) {
        // Use the dayOfWeek field directly instead of extracting from startTime
        DayOfWeek day = slot.getDayOfWeek();
        LocalTime start = slot.getStartTime().toLocalTime();
        LocalTime end = slot.getEndTime().toLocalTime();

        boolean isTuesday = day == DayOfWeek.TUESDAY && (department.equals("CLDS") || department.equals("COE"));
        boolean isThursday = day == DayOfWeek.THURSDAY && (department.equals("CST") || department.equals("CMSS"));

        return !((isTuesday || isThursday) && start.isBefore(LocalTime.of(12, 0)) && end.isAfter(LocalTime.of(10, 0)));
    }

    private boolean canAssignGeneralCourse(CourseAssignment assignment, TimeSlot slot, Map<LocalDate, Set<String>> dailyMap) {
        if (!assignment.isGeneral()) return true;

        String code = assignment.getCourse().getCourseCode().toUpperCase();
        LocalDate date = slot.getStartTime().toLocalDate();
        LocalTime time = slot.getStartTime().toLocalTime().withSecond(0).withNano(0);  // normalize

        boolean isDLD = code.contains("DLD");

        if (isDLD) {
            DayOfWeek slotDay = slot.getDayOfWeek();
            log.info("🔍 DLD checking constraints for slot: {} {} - {}",
                    slotDay,
                    slot.getStartTime().toLocalTime(),
                    slot.getEndTime().toLocalTime());

            // ✅ Ensure allowed day - use the dayOfWeek field directly
            DayOfWeek slotDay2 = slot.getDayOfWeek();
            if (!DLD_ALLOWED_DAYS.contains(slotDay2)) {
                log.info("🔍 DLD REJECTED: Day {} not in allowed days {}",
                        slotDay2, DLD_ALLOWED_DAYS);
                return false;
            }

            // ✅ Ensure allowed time (with tolerance)
            if (!time.equals(DLD_ALLOWED_TIME)) {
                log.info("🔍 DLD REJECTED: Time {} does not match required time {}",
                        time, DLD_ALLOWED_TIME);
                return false;
            }

            // ✅ Prevent duplicate DLD for same day
            Set<String> dayCourses = dailyMap.getOrDefault(date, new HashSet<>());
            if (dayCourses.contains("DLD")) {
                log.info("🔍 DLD REJECTED: Already assigned on date {}", date);
                return false;
            }

            log.info("🔍 DLD PASSED all constraint checks for slot {}", slot.getId());
        }

        if (code.contains("TMC")) {
            Set<String> dayCourses = dailyMap.getOrDefault(date, new HashSet<>());
            if (dayCourses.contains("TMC")) {
                log.debug("TMC already assigned on {}", date);
                return false;
            }
        }

        return true;
    }

    private void trackGeneralCourse(CourseAssignment assignment, TimeSlot slot, Map<LocalDate, Set<String>> dailyMap) {
        if (!assignment.isGeneral()) return;

        String code = assignment.getCourse().getCourseCode().toUpperCase();
        LocalDate date = slot.getStartTime().toLocalDate();
        Set<String> dayCourses = dailyMap.computeIfAbsent(date, k -> new HashSet<>());

        if (code.contains("DLD")) dayCourses.add("DLD");
        if (code.contains("TMC")) dayCourses.add("TMC");
    }

    private int availableOptionsCount(CourseAssignment assignment, List<TimeSlot> slots, List<Venue> venues, Map<String, Boolean> restrictionMap) {
        if (assignment == null || assignment.getLecturer() == null) return 0;

        if (assignment.getDepartment() == null && !assignment.isGeneral()) {
            log.warn("Assignment {} has no department", assignment.getCourse().getCourseCode());
        }

        int suitableVenues = (int) venues.stream()
                .filter(v -> isVenueSuitableForAssignment(v, assignment, restrictionMap))
                .count();
        return suitableVenues * slots.size();
    }

    private Map<String, Boolean> preloadVenueConstraints(List<Venue> venues) {
        log.debug("Preloading venue constraints...");
        List<Long> venueIds = venues.stream()
                .map(Venue::getId)
                .collect(Collectors.toList());

        if (venueIds.isEmpty()) {
            log.debug("No venue IDs to preload constraints for.");
            return new HashMap<>();
        }

        List<VenueConstraint> constraints;
        try {
            constraints = Optional.ofNullable(
                    venueConstraintRepository.findByVenueIdIn(venueIds)
            ).orElse(Collections.emptyList());
        } catch (Exception e) {
            log.error("Error fetching constraints from repository", e);
            throw e;
        }

        Map<String, Boolean> restrictionMap = new HashMap<>();
        for (VenueConstraint constraint : constraints) {
            if (constraint == null || constraint.getVenue() == null || constraint.getPreferredDepartment() == null) {
                log.warn("Skipping null or incomplete constraint: {}", constraint);
                continue;
            }

            String key = constraint.getVenue().getId() + "-" + constraint.getPreferredDepartment().getId();
            restrictionMap.put(key, constraint.isRestricted());
        }

        log.info("Preloaded {} venue constraints", restrictionMap.size());
        return restrictionMap;
    }
}