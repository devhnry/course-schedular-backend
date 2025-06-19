package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.CourseAssignment;
import com.henry.universitycourseschedular.models.ScheduleEntry;
import com.henry.universitycourseschedular.models.TimeSlot;
import com.henry.universitycourseschedular.models.Venue;

import java.util.List;

public interface GreedySchedulerService {
    List<ScheduleEntry> assignCourses(List<CourseAssignment> assignments, List<TimeSlot> slots, List<Venue> venues);
}

