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

    private static final Map<String, List<String>> GENERAL_COURSE_TO_VENUES = Map.of(
            "TMC", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "DLD", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "ALDC", List.of("CHAPEL", "UNIVERSITY CHAPEL"),
            "GST", List.of("LECTURE THEATRE 1"),
            "EDS", List.of("LECTURE THEATRE 1"),
            "CEDS", List.of("LECTURE THEATRE 1")
    );

    private final VenueConstraintRepository venueConstraintRepository;
    private final DepartmentRepository departmentRepository;
    private final VenueConstraintService venueConstraintService;

    @Override
    public List<ScheduleEntry> assignCourses(List<CourseAssignment> assignments, List<TimeSlot> slots, List<Venue> venues) {
        log.info("Starting course assignment...");

        List<ScheduleEntry> scheduledEntries = new ArrayList<>();
        Map<Long, Set<Long>> lecturerBusySlots = new HashMap<>();
        Map<Long, Set<Long>> venueBusySlots = new HashMap<>();
        Map<DayOfWeek, Set<String>> dailyGeneralCourseMap = new HashMap<>();

        Map<String, Boolean> restrictionMap = preloadVenueConstraints(venues);
        List<CourseAssignment> sortedAssignments = new ArrayList<>(assignments);
        sortedAssignments.sort(Comparator
                .comparing((CourseAssignment a) -> !a.getCourse().isGeneralCourse())
                .thenComparingInt(a -> availableOptionsCount(a, slots, venues, restrictionMap)));

        for (CourseAssignment assignment : sortedAssignments) {
            boolean assigned = false;
            boolean isDLD = assignment.getCourse().getCode().toUpperCase().contains("DLD");

            for (TimeSlot slot : slots) {
                if (!canAssignGeneralCourse(assignment, slot, dailyGeneralCourseMap)) continue;
                if (!canAssignDuringChapel(slot, assignment.getDepartment() != null ? assignment.getDepartment().getName() : "")) continue;

                Optional<Venue> freeVenue = venues.stream()
                        .filter(v -> isVenueSuitableForAssignment(v, assignment, restrictionMap))
                        .filter(v -> !venueBusySlots.getOrDefault(v.getId(), new HashSet<>()).contains(slot.getId()))
                        .findFirst();

                if (freeVenue.isEmpty()) continue;

                List<Lecturer> availableLecturers = assignment.getLecturers().stream()
                        .filter(l -> !lecturerBusySlots.getOrDefault(l.getId(), new HashSet<>()).contains(slot.getId()))
                        .toList();

                if (availableLecturers.isEmpty()) continue;

                Venue venue = freeVenue.get();
                ScheduleEntry entry = new ScheduleEntry();
                entry.setCourseAssignment(assignment);
                entry.getCourseAssignment().setLecturers(availableLecturers);
                entry.setTimeSlot(slot);
                entry.setVenue(venue);
//                entry.setLecturers(availableLecturers);
                scheduledEntries.add(entry);

                for (Lecturer lecturer : availableLecturers) {
                    lecturerBusySlots.computeIfAbsent(lecturer.getId(), k -> new HashSet<>()).add(slot.getId());
                }

                venueBusySlots.computeIfAbsent(venue.getId(), k -> new HashSet<>()).add(slot.getId());
                trackGeneralCourse(assignment, slot, dailyGeneralCourseMap);
                assigned = true;
                break;
            }

            if (!assigned) {
                log.warn("Could not assign course {}", assignment.getCourse().getCode());
            }
        }

        return scheduledEntries;
    }

    private boolean isVenueSuitableForAssignment(Venue venue, CourseAssignment assignment, Map<String, Boolean> restrictionMap) {
        if (venue == null || assignment == null) return false;

        if (assignment.getCourse().isGeneralCourse()) {
            String code = assignment.getCourse().getCode().toUpperCase();
            return GENERAL_COURSE_TO_VENUES.getOrDefault(code, List.of()).stream()
                    .anyMatch(venueName -> venue.getName().toUpperCase().contains(venueName));
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

    private boolean canAssignGeneralCourse(CourseAssignment assignment, TimeSlot slot, Map<DayOfWeek, Set<String>> dailyMap) {
        if (!assignment.getCourse().isGeneralCourse()) return true;

        String code = assignment.getCourse().getCode().toUpperCase();
        DayOfWeek day = slot.getDayOfWeek();
        LocalTime time = slot.getStartTime().withSecond(0).withNano(0);

        if (code.contains("DLD")) {
            if (!DLD_ALLOWED_DAYS.contains(day)) return false;
            if (!time.equals(DLD_ALLOWED_TIME)) return false;
            if (dailyMap.getOrDefault(day, new HashSet<>()).contains("DLD")) return false;
        }

        if (code.contains("TMC") && dailyMap.getOrDefault(day, new HashSet<>()).contains("TMC")) return false;

        return true;
    }

    private void trackGeneralCourse(CourseAssignment assignment, TimeSlot slot, Map<DayOfWeek, Set<String>> dailyMap) {
        if (!assignment.getCourse().isGeneralCourse()) return;
        String code = assignment.getCourse().getCode().toUpperCase();
        DayOfWeek day = slot.getDayOfWeek();
        Set<String> dayCourses = dailyMap.computeIfAbsent(day, k -> new HashSet<>());
        if (code.contains("DLD")) dayCourses.add("DLD");
        if (code.contains("TMC")) dayCourses.add("TMC");
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
}
