package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import com.henry.universitycourseschedular.models.schedule.TimeSlot;

import java.util.List;

public interface GreedySchedulerService {
    List<ScheduleEntry> assignCourses(List<CourseAssignment> assignments, List<TimeSlot> slots, List<Venue> venues);
}

