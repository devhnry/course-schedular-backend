package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.ScheduleEntry;

import java.util.List;

public interface GACConstraintSolverService {
    List<ScheduleEntry> enforceConstraints(List<ScheduleEntry> tentativeSchedule);
}

