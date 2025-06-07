package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import com.henry.universitycourseschedular.services.GACConstraintSolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class IGACConstraintSolverService implements GACConstraintSolverService {

    @Override
    public List<ScheduleEntry> enforceConstraints(List<ScheduleEntry> tentativeSchedule) {
        return List.of();
    }
}
