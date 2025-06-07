package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import com.henry.universitycourseschedular.services.SimulatedAnnealingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class ISimulatedAnnealingService implements SimulatedAnnealingService {

    @Override
    public List<ScheduleEntry> optimize(List<ScheduleEntry> initialSchedule) {
        return List.of();
    }
}
