package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import com.henry.universitycourseschedular.models.schedule.TimeSlot;
import com.henry.universitycourseschedular.models.schedule.VenueConstraint;
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


    private final VenueConstraintRepository venueConstraintRepository;

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

        log.debug("Sorting assignments...");
        try{
            assignments.sort(Comparator
                    .comparing((CourseAssignment a) -> {
                        log.info("Assignment {}", a);
                        return !a.isGeneral();
                    })
                    .thenComparingInt(a -> {
                                int count = availableOptionsCount(a, slots, venues, restrictionMap);
                                log.debug("Course {} has {} available options", a.getCourse().getCourseCode(), count);
                                return count;
                            }));
//                            availableOptionsCount(a, slots, venues, restrictionMap)));
        } catch (Exception e){
            log.error("Error loading venue constraints", e);
        }


        for (CourseAssignment assignment : assignments) {
            log.debug("Attempting to assign course: {}", assignment.getCourse().getCourseCode());
            boolean assigned = false;

            for (TimeSlot slot : slots) {
                log.trace("Checking slot: {}", slot.getId());

                if (!canAssignGeneralCourse(assignment, slot, dailyGeneralCourseMap)) {
                    log.trace("General course {} failed slot {} check", assignment.getCourse().getCourseCode(), slot.getId());
                    continue;
                }

                String deptName = assignment.getDepartment() != null ? assignment.getDepartment().getName() : "";
                if (!canAssignDuringChapel(slot, deptName)) {
                    log.trace("Slot {} violates chapel time for department {}", slot.getId(), deptName);
                    continue;
                }

                Set<Long> lecturerSlots = lecturerBusySlots.getOrDefault(assignment.getLecturer().getId(), new HashSet<>());
                if (lecturerSlots.contains(slot.getId())) {
                    log.trace("Lecturer {} is busy at slot {}", assignment.getLecturer().getId(), slot.getId());
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
                }
            }

            if (!assigned) {
                log.warn("Could not assign course {}", assignment.getCourse().getCourseCode());
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
        if (restrictionMap.isEmpty()) return true;
        if (assignment == null || assignment.getDepartment() == null || venue == null) {
            log.warn("Null venue or assignment or department in suitability check: {}", assignment);
            return false;
        }
        if (assignment.isGeneral()) return true;
        String key = venue.getId() + "-" + assignment.getDepartment().getId();
        return !restrictionMap.getOrDefault(key, false);
    }


    private boolean canAssignDuringChapel(TimeSlot slot, String department) {
        DayOfWeek day = slot.getStartTime().getDayOfWeek();
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

        if (code.contains("DLD")) {
            // ✅ Ensure allowed day
            if (!DLD_ALLOWED_DAYS.contains(slot.getStartTime().getDayOfWeek())) {
                log.debug("DLD not allowed on {}", slot.getStartTime().getDayOfWeek());
                return false;
            }

            // ✅ Ensure allowed time (with tolerance)
            if (!time.equals(DLD_ALLOWED_TIME)) {
                log.debug("DLD time mismatch: actual={}, expected={}", time, DLD_ALLOWED_TIME);
                return false;
            }

            // ✅ Prevent duplicate DLD for same day
            Set<String> dayCourses = dailyMap.getOrDefault(date, new HashSet<>());
            if (dayCourses.contains("DLD")) {
                log.debug("DLD already assigned on {}", date);
                return false;
            }
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

        if (assignment.getDepartment() == null) {
            log.warn("Assignment {} has no department", assignment.getCourse().getCourseCode());
        }

//        if (assignment == null || assignment.getDepartment() == null || assignment.getLecturer() == null) {
//            log.warn("Null field in assignment during option count: {}", assignment);
//            return 0;
//        }

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

