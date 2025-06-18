package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models._dto.ScheduleEntryDto;
import com.henry.universitycourseschedular.models._dto.TimetableDto;
import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
import com.henry.universitycourseschedular.models.schedule.Schedule;
import com.henry.universitycourseschedular.models.schedule.ScheduleEntry;
import com.henry.universitycourseschedular.models.schedule.TimeSlot;
import com.henry.universitycourseschedular.repositories.CourseAssignmentRepository;
import com.henry.universitycourseschedular.repositories.TimeSlotRepository;
import com.henry.universitycourseschedular.repositories.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimetableGeneratorServiceImpl implements TimetableGeneratorService {

    private final GreedySchedulerService greedyScheduler;
    private final SimulatedAnnealingService annealer;
    private final GACConstraintSolverService gac;

    private final CourseAssignmentRepository courseRepo;
    private final TimeSlotRepository slotRepo;
    private final VenueRepository venueRepo;

    @Override
    public TimetableDto generateTimetable() {
        log.info("Starting complete timetable generation pipeline");

        try {
            // Load data
            List<CourseAssignment> courses = courseRepo.findAll();
            List<TimeSlot> slots = slotRepo.findAll();
            List<Venue> venues = venueRepo.findAll();

            log.info("Loaded {} courses, {} time slots, {} venues",
                    courses.size(), slots.size(), venues.size());

            // Phase 1: Greedy scheduling (handles hard constraints)
            log.info("Phase 1: Running Greedy Scheduler");
            List<ScheduleEntry> greedyResult = greedyScheduler.assignCourses(courses, slots, venues);
            log.info("Greedy scheduler assigned {} out of {} courses",
                    greedyResult.size(), courses.size());

            if (greedyResult.isEmpty()) {
                log.warn("Greedy scheduler produced no assignments");
                return TimetableDto.builder()
                        .departmentName("No assignments")
                        .programCode("ERROR")
                        .days(List.of())
                        .build();
            }

            // Phase 2: Simulated Annealing (optimization)
            log.info("Phase 2: Running Simulated Annealing Optimization");
            List<ScheduleEntry> optimizedResult = annealer.optimize(greedyResult);
            log.info("Simulated annealing optimized {} assignments", optimizedResult.size());

            // Phase 3: GAC (constraint consistency)
            log.info("Phase 3: Running GAC Constraint Solver");
            List<ScheduleEntry> finalResult = gac.enforceConstraints(optimizedResult);
            log.info("GAC produced {} consistent assignments", finalResult.size());

            // Create final schedule
            Schedule schedule = new Schedule();
            schedule.setEntries(finalResult);

            log.info("Timetable generation completed successfully with {} final assignments",
                    finalResult.size());

            // Convert ScheduleEntry list to ScheduleEntryDto list
            List<ScheduleEntryDto> scheduleEntryDtos = finalResult.stream()
                    .map(entry -> new ScheduleEntryDto(
                            entry.getCourseAssignment().getCourse().getCourseCode(),
                            entry.getCourseAssignment().getLecturer().getFullName(), // Updated to use fullName
                            entry.getVenue().getName(),
                            entry.getTimeSlot().getDayOfWeek().name(),
                            entry.getTimeSlot().getStartTime(),
                            entry.getTimeSlot().getEndTime()
                    ))
                    .collect(Collectors.toList());

            // Get department and program info from first entry if available
            String departmentName = "Generated Schedule";
            String programCode = "ALL";

            if (!finalResult.isEmpty()) {
                ScheduleEntry firstEntry = finalResult.get(0);
                if (firstEntry.getCourseAssignment().getCourse().getProgram() != null) {
                    departmentName = firstEntry.getCourseAssignment().getCourse().getProgram().getDepartment().getName();
                    programCode = firstEntry.getCourseAssignment().getCourse().getProgram().getCode();
                }
            }

            return TimetableDto.builder()
                    .departmentName(departmentName)
                    .programCode(programCode)
                    .days(scheduleEntryDtos)
                    .build();

        } catch (Exception e) {
            log.error("Error during timetable generation", e);
            throw new RuntimeException("Timetable generation failed: " + e.getMessage(), e);
        }
    }
}
