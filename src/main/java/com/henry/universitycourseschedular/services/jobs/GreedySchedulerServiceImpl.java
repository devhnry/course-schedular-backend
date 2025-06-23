package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.*;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.VenueConstraintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
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
    private static final LocalTime BREAK_START = LocalTime.of(14, 0); // 2:00 PM
    private static final LocalTime BREAK_END = LocalTime.of(15, 0);   // 3:00 PM

    // Updated venue mappings based on the HTML timetable
    private static final Map<String, List<String>> GENERAL_COURSE_TO_VENUES = Map.of(
            "TMC", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "DLD", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "ALDC", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "GST", List.of("LECTURE THEATRE"),
            "EDS", List.of("LECTURE THEATRE"),
            "CEDS", List.of("LECTURE THEATRE")
    );

    private final VenueConstraintRepository venueConstraintRepository;
    private final DepartmentRepository departmentRepository;
    private final VenueConstraintService venueConstraintService;

    @Override
    public List<ScheduleEntry> assignCourses(List<CourseAssignment> assignments, List<TimeSlot> slots, List<Venue> venues) {
        log.info("Starting course assignment with {} assignments, {} slots, {} venues",
                assignments.size(), slots.size(), venues.size());

        List<ScheduleEntry> scheduledEntries = new ArrayList<>();
        Map<Long, Set<Long>> lecturerBusySlots = new HashMap<>();
        Map<Long, Set<Long>> venueBusySlots = new HashMap<>();

        // Track general courses more comprehensively
        Map<DayOfWeek, Map<String, Set<LocalTime>>> dailyGeneralCourseSchedule = new HashMap<>();

        Map<String, Boolean> restrictionMap = preloadVenueConstraints(venues);

        // Separate general and regular courses for better handling
        List<CourseAssignment> generalCourses = assignments.stream()
                .filter(a -> a.getCourse().isGeneralCourse())
                .collect(Collectors.toList());

        List<CourseAssignment> regularCourses = assignments.stream()
                .filter(a -> !a.getCourse().isGeneralCourse())
                .collect(Collectors.toList());

        // Sort general courses by priority (DLD first, then others)
        generalCourses.sort(Comparator
                .comparing((CourseAssignment a) -> !a.getCourse().getCode().toUpperCase().contains("DLD"))
                .thenComparing(a -> a.getCourse().getCode()));

        // Sort regular courses by constraint difficulty
        regularCourses.sort(Comparator
                .comparingInt(a -> availableOptionsCount(a, slots, venues, restrictionMap)));

        log.info("Processing {} general courses first", generalCourses.size());

        // Process general courses first
        for (CourseAssignment assignment : generalCourses) {
            if (assignGeneralCourse(assignment, slots, venues, scheduledEntries,
                    lecturerBusySlots, venueBusySlots, dailyGeneralCourseSchedule)) {
                log.info("Successfully assigned general course: {}", assignment.getCourse().getCode());
            } else {
                log.warn("Failed to assign general course: {} - checking constraints", assignment.getCourse().getCode());
                // Debug DLD assignment issues
                if (assignment.getCourse().getCode().toUpperCase().contains("DLD")) {
                    debugDLDAssignment(assignment, slots, venues, dailyGeneralCourseSchedule);
                }
            }
        }

        log.info("Processing {} regular courses", regularCourses.size());

        // Process regular courses
        for (CourseAssignment assignment : regularCourses) {
            if (assignRegularCourse(assignment, slots, venues, scheduledEntries,
                    lecturerBusySlots, venueBusySlots, restrictionMap)) {
                log.info("Successfully assigned regular course: {}", assignment.getCourse().getCode());
            } else {
                log.warn("Failed to assign regular course: {}", assignment.getCourse().getCode());
            }
        }

        log.info("Course assignment completed. Scheduled {} out of {} courses",
                scheduledEntries.size(), assignments.size());

        return scheduledEntries;
    }

    private void debugDLDAssignment(CourseAssignment assignment, List<TimeSlot> slots, List<Venue> venues,
                                    Map<DayOfWeek, Map<String, Set<LocalTime>>> dailySchedule) {
        log.debug("Debugging DLD assignment for course: {}", assignment.getCourse().getCode());

        // Check suitable venues
        List<Venue> suitableVenues = getSuitableVenuesForGeneralCourse("DLD", venues);
        log.debug("Found {} suitable venues for DLD: {}",
                suitableVenues.size(),
                suitableVenues.stream().map(Venue::getName).collect(Collectors.toList()));

        // Check suitable slots
        List<TimeSlot> suitableSlots = getSuitableSlotsForGeneralCourse("DLD", slots, dailySchedule);
        log.debug("Found {} suitable time slots for DLD", suitableSlots.size());

        for (TimeSlot slot : suitableSlots) {
            log.debug("DLD slot: {} at {}", slot.getDayOfWeek(), slot.getStartTime());
        }
    }

    private boolean assignGeneralCourse(CourseAssignment assignment, List<TimeSlot> slots, List<Venue> venues,
                                        List<ScheduleEntry> scheduledEntries, Map<Long, Set<Long>> lecturerBusySlots,
                                        Map<Long, Set<Long>> venueBusySlots,
                                        Map<DayOfWeek, Map<String, Set<LocalTime>>> dailyGeneralCourseSchedule) {

        String courseCode = assignment.getCourse().getCode().toUpperCase();
        log.debug("Attempting to assign general course: {}", courseCode);

        // Get suitable venues for this general course
        List<Venue> suitableVenues = getSuitableVenuesForGeneralCourse(courseCode, venues);
        if (suitableVenues.isEmpty()) {
            log.warn("No suitable venues found for general course: {}", courseCode);
            return false;
        }

        // Get suitable time slots for this general course
        List<TimeSlot> suitableSlots = getSuitableSlotsForGeneralCourse(courseCode, slots, dailyGeneralCourseSchedule);
        if (suitableSlots.isEmpty()) {
            log.warn("No suitable time slots found for general course: {}", courseCode);
            return false;
        }

        // Try to assign the course
        for (TimeSlot slot : suitableSlots) {
            // Check break time constraints for general courses too
            if (isBreakTime(slot)) {
                continue;
            }

            for (Venue venue : suitableVenues) {
                // Check if venue is available
                if (venueBusySlots.getOrDefault(venue.getId(), new HashSet<>()).contains(slot.getId())) {
                    continue;
                }

                // Check if lecturers are available
                List<Lecturer> availableLecturers = assignment.getLecturers().stream()
                        .filter(l -> !lecturerBusySlots.getOrDefault(l.getId(), new HashSet<>()).contains(slot.getId()))
                        .collect(Collectors.toList());

                if (availableLecturers.isEmpty()) {
                    continue;
                }

                // Create schedule entry
                ScheduleEntry entry = new ScheduleEntry();
                entry.setCourseAssignment(assignment);
                entry.getCourseAssignment().setLecturers(availableLecturers);
                entry.setTimeSlot(slot);
                entry.setVenue(venue);
                scheduledEntries.add(entry);

                // Mark resources as busy
                for (Lecturer lecturer : availableLecturers) {
                    lecturerBusySlots.computeIfAbsent(lecturer.getId(), k -> new HashSet<>()).add(slot.getId());
                }
                venueBusySlots.computeIfAbsent(venue.getId(), k -> new HashSet<>()).add(slot.getId());

                // Track the general course assignment
                trackGeneralCourseAssignment(courseCode, slot, dailyGeneralCourseSchedule);

                return true;
            }
        }

        return false;
    }

    private boolean assignRegularCourse(CourseAssignment assignment, List<TimeSlot> slots, List<Venue> venues,
                                        List<ScheduleEntry> scheduledEntries, Map<Long, Set<Long>> lecturerBusySlots,
                                        Map<Long, Set<Long>> venueBusySlots, Map<String, Boolean> restrictionMap) {

        for (TimeSlot slot : slots) {
            // Check chapel time constraints
            if (!canAssignDuringChapel(slot, assignment.getDepartment() != null ? assignment.getDepartment().getName() : "")) {
                continue;
            }

            // Check break time constraints (no classes 2pm-3pm)
            if (isBreakTime(slot)) {
                continue;
            }

            // Check TMC sequencing constraint (TMC must come after DLD)
            if (!canAssignTMCAfterDLD(assignment, slot, scheduledEntries)) {
                continue;
            }

            // Find suitable venue
            Optional<Venue> freeVenue = venues.stream()
                    .filter(v -> isVenueSuitableForAssignment(v, assignment, restrictionMap))
                    .filter(v -> !venueBusySlots.getOrDefault(v.getId(), new HashSet<>()).contains(slot.getId()))
                    .findFirst();

            if (freeVenue.isEmpty()) continue;

            // Check lecturer availability
            List<Lecturer> availableLecturers = assignment.getLecturers().stream()
                    .filter(l -> !lecturerBusySlots.getOrDefault(l.getId(), new HashSet<>()).contains(slot.getId()))
                    .collect(Collectors.toList());

            if (availableLecturers.isEmpty()) continue;

            // Create schedule entry
            Venue venue = freeVenue.get();
            ScheduleEntry entry = new ScheduleEntry();
            entry.setCourseAssignment(assignment);
            entry.getCourseAssignment().setLecturers(availableLecturers);
            entry.setTimeSlot(slot);
            entry.setVenue(venue);
            scheduledEntries.add(entry);

            // Mark resources as busy
            for (Lecturer lecturer : availableLecturers) {
                lecturerBusySlots.computeIfAbsent(lecturer.getId(), k -> new HashSet<>()).add(slot.getId());
            }
            venueBusySlots.computeIfAbsent(venue.getId(), k -> new HashSet<>()).add(slot.getId());

            return true;
        }

        return false;
    }

    private List<Venue> getSuitableVenuesForGeneralCourse(String courseCode, List<Venue> venues) {
        List<String> allowedVenueNames = new ArrayList<>();

        // Map course codes to their allowed venues
        if (courseCode.startsWith("TMC")) {
            allowedVenueNames.addAll(List.of("CHAPEL", "UNIVERSITY CHAPEL"));
        } else if (courseCode.startsWith("DLD") || courseCode.startsWith("ALDC")) {
            allowedVenueNames.addAll(List.of("CHAPEL", "UNIVERSITY CHAPEL"));
        } else if (courseCode.startsWith("GST") || courseCode.startsWith("EDS") || courseCode.startsWith("CEDS")) {
            allowedVenueNames.add("LECTURE THEATRE");
        }

        if (allowedVenueNames.isEmpty()) {
            log.warn("No venue mapping found for general course: {}", courseCode);
            return new ArrayList<>();
        }

        List<Venue> matchedVenues = venues.stream()
                .filter(venue -> allowedVenueNames.stream()
                        .anyMatch(allowedName -> venue.getName().toUpperCase().contains(allowedName.toUpperCase())))
                .collect(Collectors.toList());

        log.debug("Found {} venues for course {}: {}",
                matchedVenues.size(),
                courseCode,
                matchedVenues.stream().map(Venue::getName).collect(Collectors.toList()));

        return matchedVenues;
    }

    private List<TimeSlot> getSuitableSlotsForGeneralCourse(String courseCode, List<TimeSlot> slots,
                                                            Map<DayOfWeek, Map<String, Set<LocalTime>>> dailySchedule) {
        List<TimeSlot> suitableSlots = new ArrayList<>();

        for (TimeSlot slot : slots) {
            if (isSlotSuitableForGeneralCourse(courseCode, slot, dailySchedule)) {
                suitableSlots.add(slot);
            }
        }

        // Sort slots by preference (earlier times first for most courses)
        suitableSlots.sort(Comparator.comparing(TimeSlot::getDayOfWeek)
                .thenComparing(TimeSlot::getStartTime));

        return suitableSlots;
    }

    private boolean isSlotSuitableForGeneralCourse(String courseCode, TimeSlot slot,
                                                   Map<DayOfWeek, Map<String, Set<LocalTime>>> dailySchedule) {
        DayOfWeek day = slot.getDayOfWeek();
        LocalTime startTime = slot.getStartTime();

        // DLD specific constraints
        if (courseCode.startsWith("DLD")) {
            // DLD can only be on Tuesday or Thursday at 12:00 PM
            if (!DLD_ALLOWED_DAYS.contains(day)) {
                return false;
            }
            if (!startTime.equals(DLD_ALLOWED_TIME)) {
                return false;
            }
            // Check if DLD is already scheduled on this day
            return !isDLDAlreadyScheduled(day, dailySchedule);
        }

        // TMC specific constraints
        if (courseCode.startsWith("TMC")) {
            // Only one TMC per day
            return !isTMCAlreadyScheduled(day, dailySchedule);
        }

        // For other general courses, avoid conflicts with chapel times
        if (isChapelTime(day, startTime)) {
            return false;
        }

        return true;
    }

    private boolean isDLDAlreadyScheduled(DayOfWeek day, Map<DayOfWeek, Map<String, Set<LocalTime>>> dailySchedule) {
        return dailySchedule.getOrDefault(day, new HashMap<>())
                .getOrDefault("DLD", new HashSet<>())
                .contains(DLD_ALLOWED_TIME);
    }

    private boolean isTMCAlreadyScheduled(DayOfWeek day, Map<DayOfWeek, Map<String, Set<LocalTime>>> dailySchedule) {
        return dailySchedule.getOrDefault(day, new HashMap<>())
                .containsKey("TMC");
    }

    private boolean isChapelTime(DayOfWeek day, LocalTime time) {
        // Chapel times: Tuesday and Thursday 10:00-12:00
        if ((day == DayOfWeek.TUESDAY || day == DayOfWeek.THURSDAY)) {
            return time.isAfter(LocalTime.of(9, 59)) && time.isBefore(LocalTime.of(12, 1));
        }
        return false;
    }

    private void trackGeneralCourseAssignment(String courseCode, TimeSlot slot,
                                              Map<DayOfWeek, Map<String, Set<LocalTime>>> dailySchedule) {
        DayOfWeek day = slot.getDayOfWeek();
        LocalTime time = slot.getStartTime();

        String courseType;
        if (courseCode.startsWith("DLD")) {
            courseType = "DLD";
        } else if (courseCode.startsWith("TMC")) {
            courseType = "TMC";
        } else {
            courseType = "GENERAL";
        }

        dailySchedule.computeIfAbsent(day, k -> new HashMap<>())
                .computeIfAbsent(courseType, k -> new HashSet<>())
                .add(time);
    }

    private boolean isVenueSuitableForAssignment(Venue venue, CourseAssignment assignment, Map<String, Boolean> restrictionMap) {
        if (venue == null || assignment == null) return false;

        if (assignment.getCourse().isGeneralCourse()) {
            String code = assignment.getCourse().getCode().toUpperCase();
            return GENERAL_COURSE_TO_VENUES.entrySet().stream()
                    .filter(entry -> code.startsWith(entry.getKey()))
                    .flatMap(entry -> entry.getValue().stream())
                    .anyMatch(venueName -> venue.getName().toUpperCase().contains(venueName.toUpperCase()));
        }

        if (assignment.getDepartment() == null) return false;
        if (venue.getCollegeBuilding() == null) return false;

        if (assignment.getProgram() != null && !venueConstraintService.isVenueAllowedForProgram(venue.getId(), assignment.getProgram().getName())) {
            return false;
        }

        String key = venue.getId() + "-" + assignment.getDepartment().getId();
        boolean isRestricted = restrictionMap.getOrDefault(key, false);

        if (!restrictionMap.containsKey(key)) {
            return assignment.getDepartment().getCollegeBuilding() != null
                    && venue.getCollegeBuilding() != null
                    && assignment.getDepartment().getCollegeBuilding().getId().equals(venue.getCollegeBuilding().getId());
        }

        return !isRestricted;
    }

    private boolean canAssignDuringChapel(TimeSlot slot, String department) {
        DayOfWeek day = slot.getDayOfWeek();
        LocalTime start = slot.getStartTime();
        LocalTime end = slot.getEndTime();
        boolean isTuesday = day == DayOfWeek.TUESDAY && (department.equals("CLDS") || department.equals("COE"));
        boolean isThursday = day == DayOfWeek.THURSDAY && (department.equals("CST") || department.equals("CMSS"));
        return !(isTuesday || isThursday) || !(start.isBefore(LocalTime.of(12, 0)) && end.isAfter(LocalTime.of(10, 0)));
    }

    private int availableOptionsCount(CourseAssignment assignment, List<TimeSlot> slots, List<Venue> venues, Map<String, Boolean> restrictionMap) {
        if (assignment == null || assignment.getLecturers() == null || assignment.getLecturers().isEmpty()) return 0;
        return (int) venues.stream()
                .filter(v -> isVenueSuitableForAssignment(v, assignment, restrictionMap))
                .count() * slots.size();
    }

    private Map<String, Boolean> preloadVenueConstraints(List<Venue> venues) {
        List<Long> venueIds = venues.stream().map(Venue::getId).collect(Collectors.toList());
        if (venueIds.isEmpty()) return new HashMap<>();
        List<VenueConstraint> constraints = venueConstraintRepository.findByVenueIdIn(venueIds);
        Map<String, Boolean> restrictionMap = new HashMap<>();
        for (VenueConstraint vc : constraints) {
            if (vc.getVenue() == null || vc.getPreferredDepartment() == null) continue;
            restrictionMap.put(vc.getVenue().getId() + "-" + vc.getPreferredDepartment().getId(), vc.isRestricted());
        }
        return restrictionMap;
    }

    private boolean isBreakTime(TimeSlot slot) {
        LocalTime startTime = slot.getStartTime();
        LocalTime endTime = slot.getEndTime();

        // Check if the class overlaps with break time (2pm-3pm)
        return !(endTime.isBefore(BREAK_START) || startTime.isAfter(BREAK_END));
    }

    private boolean canAssignTMCAfterDLD(CourseAssignment assignment, TimeSlot slot,
                                         List<ScheduleEntry> scheduledEntries) {
        String courseCode = assignment.getCourse().getCode().toUpperCase();

        // Only apply this constraint to TMC courses
        if (!courseCode.startsWith("TMC")) {
            return true;
        }

        DayOfWeek day = slot.getDayOfWeek();
        LocalTime tmcTime = slot.getStartTime();

        // Check if there's a DLD scheduled on the same day before this TMC time
        boolean dldScheduledBefore = scheduledEntries.stream()
                .filter(entry -> entry.getCourseAssignment().getCourse().getCode().toUpperCase().startsWith("DLD"))
                .filter(entry -> entry.getTimeSlot().getDayOfWeek() == day)
                .anyMatch(entry -> entry.getTimeSlot().getEndTime().isBefore(tmcTime) ||
                        entry.getTimeSlot().getEndTime().equals(tmcTime));

        return dldScheduledBefore;
    }
}