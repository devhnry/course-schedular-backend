package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import com.henry.universitycourseschedular.models.schedule.TimeSlot;
import com.henry.universitycourseschedular.services.GreedySchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service @RequiredArgsConstructor @Slf4j
public class IGreedySchedulerService implements GreedySchedulerService {

    @Override
    public List<ScheduleEntry> assignCourses(
            List<CourseAssignment> assignments,
            List<TimeSlot> slots,
            List<Venue> venues)
    {
        List<ScheduleEntry> result = new ArrayList<>();

        // Greedy heuristic: iterate each assignment, pick first available slot and venue
        Map<Venue, Set<TimeSlot>> booked = new HashMap<>();

        for (CourseAssignment ca : assignments) {
            boolean placed = false;
            for (TimeSlot ts : slots) {
                for (Venue v : venues) {

                    // capacity and department constraint
                    if (v.getCapacity() < ca.getCourse().getExpectedStudents()) continue;
                    if (!v.getCollegeBuilding().equals(ca.getCourse().getDepartment().getCollegeBuilding())) continue;

                    Set<TimeSlot> occupied = booked.computeIfAbsent(v, k -> new HashSet<>());
                    if (occupied.contains(ts)) continue;

                    // assign entry
                    ScheduleEntry entry = ScheduleEntry.builder()
                            .courseAssignment(ca)
                            .timeSlot(ts)
                            .venue(v)
                            .build();
                    result.add(entry);
                    occupied.add(ts);
                    placed = true;
                    break;
                }
                if (placed) break;
            }
            if (!placed) {
                log.warn("Unscheduled assignment: {}", ca.getId());
            }
        }
        return result;
    }
}
