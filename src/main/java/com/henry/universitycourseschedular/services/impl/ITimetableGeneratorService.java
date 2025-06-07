package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.models._dto.TimetableDTO;
import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.models.schedule.Schedule;
import com.henry.universitycourseschedular.models.schedule.TimeSlot;
import com.henry.universitycourseschedular.repositories.CourseAssignmentRepository;
import com.henry.universitycourseschedular.repositories.TimeSlotRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import com.henry.universitycourseschedular.services.GACConstraintSolverService;
import com.henry.universitycourseschedular.services.GreedySchedulerService;
import com.henry.universitycourseschedular.services.SimulatedAnnealingService;
import com.henry.universitycourseschedular.services.TimetableGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ITimetableGeneratorService implements TimetableGeneratorService {
    private final GreedySchedulerService greedyScheduler;
    private final SimulatedAnnealingService annealer;
    private final GACConstraintSolverService gac;

    private final CourseAssignmentRepository courseRepo;
    private final TimeSlotRepository slotRepo;
    private final VenueRepository venueRepo;

    @Override
    public TimetableDTO generateTimetable() {
        List<CourseAssignment> courses = courseRepo.findAll();
        List<TimeSlot> slots = slotRepo.findAll();
        List<Venue> venues = venueRepo.findAll();

        var greedy = greedyScheduler.assignCourses(courses, slots, venues);
        var annealed = annealer.optimize(greedy);
        var consistent = gac.enforceConstraints(annealed);

        Schedule schedule = new Schedule();
        schedule.setEntries(consistent);

//        return TimetableDTO.from(schedule);
        return TimetableDTO.builder().build();
    }
}

