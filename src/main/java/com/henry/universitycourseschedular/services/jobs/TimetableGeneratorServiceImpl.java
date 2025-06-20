package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models._dto.TimetableDto;
import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.models.schedule.Schedule;
import com.henry.universitycourseschedular.models.schedule.TimeSlot;
import com.henry.universitycourseschedular.repositories.CourseAssignmentRepository;
import com.henry.universitycourseschedular.repositories.TimeSlotRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableGeneratorServiceImpl implements TimetableGeneratorService {
    private final GreedySchedulerService greedyScheduler;
    private final SimulatedAnnealingService annealer;
    private final GACConstraintSolverService gac;

    private final CourseAssignmentRepository courseRepo;
    private final TimeSlotRepository slotRepo;
    private final VenueRepository venueRepo;

    @Override
    public TimetableDto generateTimetable() {
        List<CourseAssignment> courses = courseRepo.findAll();
        List<TimeSlot> slots = slotRepo.findAll();
        List<Venue> venues = venueRepo.findAll();

        var greedy = greedyScheduler.assignCourses(courses, slots, venues);
        var annealed = annealer.optimize(greedy);
        var consistent = gac.enforceConstraints(annealed);

        Schedule schedule = new Schedule();
        schedule.setEntries(consistent);

//        return TimetableDTO.from(schedule);
        return TimetableDto.builder().build();
    }
}

