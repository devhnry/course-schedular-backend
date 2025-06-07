package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;

import java.util.List;

public interface SimulatedAnnealingService {
    List<ScheduleEntry> optimize(List<ScheduleEntry> initialSchedule);
}