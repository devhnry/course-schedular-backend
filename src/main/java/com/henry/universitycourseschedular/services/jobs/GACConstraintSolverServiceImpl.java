package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
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
                        entry.getCourseAssignment().getCourse().getCourseCode(),
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
        if (course.getCourseCode().toUpperCase().contains("DLD")) {
            DayOfWeek day = timeSlot.getDayOfWeek();
            LocalTime startTime = timeSlot.getStartTime().toLocalTime();

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
        if (course.getCourseCode().toUpperCase().startsWith("TMC")) {
            // Sports courses (ending in 2) should not be scheduled
            if (course.isSportsCourse()) {
                log.debug("Sports course should not be scheduled: {}", course.getCourseCode());
                return false;
            }
        }

        return true;
    }

    private boolean hasConflicts(ScheduleEntry entry, List<ScheduleEntry> existingEntries) {
        for (ScheduleEntry existing : existingEntries) {
            if (existing.getTimeSlot().getId().equals(entry.getTimeSlot().getId())) {
                // Same time slot - check for conflicts

                // Lecturer conflict
                if (existing.getCourseAssignment().getLecturer().getId()
                        .equals(entry.getCourseAssignment().getLecturer().getId())) {
                    log.debug("Lecturer conflict detected for lecturer {}",
                            entry.getCourseAssignment().getLecturer().getId());
                    return true;
                }

                // Venue conflict
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
                        violating.getCourseAssignment().getCourse().getCourseCode());
            } else {
                log.warn("Could not resolve violation for course {}",
                        violating.getCourseAssignment().getCourse().getCourseCode());
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
        // Apply arc consistency to ensure all constraints are satisfied
        List<ScheduleEntry> consistent = new ArrayList<>();

        // Group by time slot to check consistency
        Map<Long, List<ScheduleEntry>> byTimeSlot = entries.stream()
                .collect(Collectors.groupingBy(e -> e.getTimeSlot().getId()));

        for (Map.Entry<Long, List<ScheduleEntry>> slotGroup : byTimeSlot.entrySet()) {
            List<ScheduleEntry> slotEntries = slotGroup.getValue();

            // Ensure no conflicts within the same time slot
            Set<Long> usedLecturers = new HashSet<>();
            Set<Long> usedVenues = new HashSet<>();

            for (ScheduleEntry entry : slotEntries) {
                Long lecturerId = entry.getCourseAssignment().getLecturer().getId();
                Long venueId = entry.getVenue().getId();

                if (!usedLecturers.contains(lecturerId) && !usedVenues.contains(venueId)) {
                    consistent.add(entry);
                    usedLecturers.add(lecturerId);
                    usedVenues.add(venueId);
                } else {
                    log.debug("Dropping conflicting entry: {}",
                            entry.getCourseAssignment().getCourse().getCourseCode());
                }
            }
        }

        return consistent;
    }
}