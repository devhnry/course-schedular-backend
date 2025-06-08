package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class GACConstraintSolverServiceImpl implements GACConstraintSolverService {

    @Override
    public List<ScheduleEntry> enforceConstraints(List<ScheduleEntry> tentativeSchedule) {
        return List.of();
    }
}
