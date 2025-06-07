package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;

import java.util.List;

public interface SimulatedAnnealingService {
    List<ScheduleEntry> optimize(List<ScheduleEntry> initialSchedule);
}