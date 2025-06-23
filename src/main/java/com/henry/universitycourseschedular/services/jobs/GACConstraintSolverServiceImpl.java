package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.Lecturer;
import com.henry.universitycourseschedular.models.ScheduleEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GACConstraintSolverServiceImpl implements GACConstraintSolverService {

    @Override
    public List<ScheduleEntry> enforceConstraints(List<ScheduleEntry> tentativeSchedule) {
        if (tentativeSchedule == null || tentativeSchedule.isEmpty()) {
            log.warn("No tentative schedule provided for constraint enforcement");
            return new ArrayList<>();
        }

        log.info("Starting GAC constraint enforcement on {} entries", tentativeSchedule.size());

        List<ScheduleEntry> validEntries = new ArrayList<>();
        List<ScheduleEntry> violatingEntries = new ArrayList<>();

        // Phase 1: Identify constraint violations
        for (ScheduleEntry entry : tentativeSchedule) {
            if (isValidEntry(entry, validEntries)) {
                validEntries.add(entry);
            } else {
                violatingEntries.add(entry);
                log.debug("Entry violates constraints: {} at {}",
                        entry.getCourseAssignment().getCourse().getCode(),
                        entry.getTimeSlot().getStartTime());
            }
        }

        // Phase 2: Try to resolve violations
        List<ScheduleEntry> resolvedEntries = resolveViolations(violatingEntries, validEntries);
        validEntries.addAll(resolvedEntries);

        // Phase 3: Apply arc consistency
        List<ScheduleEntry> consistentSchedule = applyArcConsistency(validEntries);

        log.info("GAC completed. Valid entries: {}, Violations resolved: {}, Final consistent: {}",
                validEntries.size(), resolvedEntries.size(), consistentSchedule.size());

        return consistentSchedule;
    }

    private boolean isValidEntry(ScheduleEntry entry, List<ScheduleEntry> existingEntries) {
        // Check hard constraints
        if (!checkHardConstraints(entry)) {
            return false;
        }

        // Check conflicts with existing entries
        return !hasConflicts(entry, existingEntries);
    }

    private boolean checkHardConstraints(ScheduleEntry entry) {
        var course = entry.getCourseAssignment().getCourse();
        var timeSlot = entry.getTimeSlot();

        // DLD constraints
        if (course.getCode().toUpperCase().contains("DLD")) {
            DayOfWeek day = timeSlot.getDayOfWeek();
            LocalTime startTime = timeSlot.getStartTime();

            if (day != DayOfWeek.TUESDAY && day != DayOfWeek.THURSDAY) {
                log.debug("DLD constraint violation: wrong day {}", day);
                return false;
            }

            if (!startTime.equals(LocalTime.of(12, 0))) {
                log.debug("DLD constraint violation: wrong time {}", startTime);
                return false;
            }
        }

        // TMC constraints
        if (course.getCode().toUpperCase().startsWith("TMC")) {
            // Sports courses (ending in 2) should not be scheduled
            if (course.isSportsCourse()) {
                log.debug("Sports course should not be scheduled: {}", course.getCode());
                return false;
            }
        }

        return true;
    }

    private boolean hasConflicts(ScheduleEntry entry, List<ScheduleEntry> existingEntries) {
        for (ScheduleEntry existing : existingEntries) {
            if (existing.getTimeSlot().getId().equals(entry.getTimeSlot().getId())) {

                Set<Long> entryLecturerIds = entry.getCourseAssignment().getLecturers().stream()
                        .map(Lecturer::getId)
                        .collect(Collectors.toSet());

                Set<Long> existingLecturerIds = existing.getCourseAssignment().getLecturers().stream()
                        .map(Lecturer::getId)
                        .collect(Collectors.toSet());

                // Check lecturer overlap
                Set<Long> overlap = new HashSet<>(entryLecturerIds);
                overlap.retainAll(existingLecturerIds);
                if (!overlap.isEmpty()) {
                    log.debug("Lecturer conflict detected between entries in same time slot");
                    return true;
                }

                if (existing.getVenue().getId().equals(entry.getVenue().getId())) {
                    log.debug("Venue conflict detected for venue {}", entry.getVenue().getId());
                    return true;
                }
            }
        }

        return false;
    }


    private List<ScheduleEntry> resolveViolations(List<ScheduleEntry> violatingEntries,
                                                  List<ScheduleEntry> validEntries) {
        List<ScheduleEntry> resolved = new ArrayList<>();

        for (ScheduleEntry violating : violatingEntries) {
            // Try to find alternative time slots or venues
            ScheduleEntry resolvedEntry = findAlternativeSlot(violating, validEntries);
            if (resolvedEntry != null) {
                resolved.add(resolvedEntry);
                log.debug("Resolved violation for course {}",
                        violating.getCourseAssignment().getCourse().getCode());
            } else {
                log.warn("Could not resolve violation for course {}",
                        violating.getCourseAssignment().getCourse().getCode());
            }
        }

        return resolved;
    }

    private ScheduleEntry findAlternativeSlot(ScheduleEntry violating, List<ScheduleEntry> validEntries) {
        // This is a simplified resolution strategy
        // In a full implementation, this would use more sophisticated algorithms

        // For now, just return null (couldn't resolve)
        // TODO: Implement proper alternative slot finding
        return null;
    }

    private List<ScheduleEntry> applyArcConsistency(List<ScheduleEntry> entries) {
        List<ScheduleEntry> consistent = new ArrayList<>();

        Map<Long, List<ScheduleEntry>> byTimeSlot = entries.stream()
                .collect(Collectors.groupingBy(e -> e.getTimeSlot().getId()));

        for (Map.Entry<Long, List<ScheduleEntry>> slotGroup : byTimeSlot.entrySet()) {
            List<ScheduleEntry> slotEntries = slotGroup.getValue();
            Set<Long> usedLecturerIds = new HashSet<>();
            Set<Long> usedVenueIds = new HashSet<>();

            for (ScheduleEntry entry : slotEntries) {
                boolean lecturerConflict = entry.getCourseAssignment().getLecturers().stream()
                        .anyMatch(l -> usedLecturerIds.contains(l.getId()));
                boolean venueConflict = usedVenueIds.contains(entry.getVenue().getId());

                if (!lecturerConflict && !venueConflict) {
                    consistent.add(entry);
                    entry.getCourseAssignment().getLecturers().forEach(l -> usedLecturerIds.add(l.getId()));
                    usedVenueIds.add(entry.getVenue().getId());
                } else {
                    log.debug("Dropping conflicting entry: {}", entry.getCourseAssignment().getCourse().getCode());
                }
            }
        }

        return consistent;
    }

}