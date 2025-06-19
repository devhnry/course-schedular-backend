package com.henry.universitycourseschedular.services.jobs;

import com.henry.universitycourseschedular.models.Lecturer;
import com.henry.universitycourseschedular.models.ScheduleEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulatedAnnealingServiceImpl implements SimulatedAnnealingService {

    private static final double INITIAL_TEMPERATURE = 1000.0;
    private static final double COOLING_RATE = 0.95;
    private static final double MIN_TEMPERATURE = 1.0;
    private static final int MAX_ITERATIONS = 1000;

    @Override
    public List<ScheduleEntry> optimize(List<ScheduleEntry> initialSchedule) {
        if (initialSchedule == null || initialSchedule.isEmpty()) {
            log.warn("No initial schedule provided for optimization");
            return new ArrayList<>();
        }

        log.info("Starting Simulated Annealing optimization with {} entries", initialSchedule.size());

        List<ScheduleEntry> currentSolution = new ArrayList<>(initialSchedule);
        List<ScheduleEntry> bestSolution = new ArrayList<>(currentSolution);

        double currentCost = calculateCost(currentSolution);
        double bestCost = currentCost;
        double temperature = INITIAL_TEMPERATURE;

        Random random = new Random();
        int iteration = 0;

        while (temperature > MIN_TEMPERATURE && iteration < MAX_ITERATIONS) {
            // Generate neighbor solution by swapping two random entries
            List<ScheduleEntry> neighborSolution = generateNeighbor(currentSolution, random);
            double neighborCost = calculateCost(neighborSolution);

            // Accept or reject the neighbor solution
            if (shouldAccept(currentCost, neighborCost, temperature, random)) {
                currentSolution = neighborSolution;
                currentCost = neighborCost;

                // Update best solution if current is better
                if (currentCost < bestCost) {
                    bestSolution = new ArrayList<>(currentSolution);
                    bestCost = currentCost;
                    log.debug("New best solution found with cost: {}", bestCost);
                }
            }

            // Cool down
            temperature *= COOLING_RATE;
            iteration++;

            if (iteration % 100 == 0) {
                log.debug("SA Iteration {}: Temperature={}, Current Cost={}, Best Cost={}",
                        iteration, temperature, currentCost, bestCost);
            }
        }

        log.info("Simulated Annealing completed. Initial cost: {}, Final cost: {}, Improvement: {}%",
                calculateCost(initialSchedule), bestCost,
                ((calculateCost(initialSchedule) - bestCost) / calculateCost(initialSchedule)) * 100);

        return bestSolution;
    }

    private List<ScheduleEntry> generateNeighbor(List<ScheduleEntry> solution, Random random) {
        List<ScheduleEntry> neighbor = new ArrayList<>(solution);

        if (neighbor.size() < 2) {
            return neighbor;
        }

        // Simple neighbor generation: swap time slots of two random entries
        int index1 = random.nextInt(neighbor.size());
        int index2 = random.nextInt(neighbor.size());

        if (index1 != index2) {
            ScheduleEntry entry1 = neighbor.get(index1);
            ScheduleEntry entry2 = neighbor.get(index2);

            // Swap time slots
            var tempTimeSlot = entry1.getTimeSlot();
            entry1.setTimeSlot(entry2.getTimeSlot());
            entry2.setTimeSlot(tempTimeSlot);
        }

        return neighbor;
    }

    private boolean shouldAccept(double currentCost, double neighborCost, double temperature, Random random) {
        if (neighborCost < currentCost) {
            return true; // Always accept better solutions
        }

        // Accept worse solutions with probability based on temperature
        double probability = Math.exp(-(neighborCost - currentCost) / temperature);
        return random.nextDouble() < probability;
    }

    private double calculateCost(List<ScheduleEntry> schedule) {
        double cost = 0.0;

        // Cost factors:
        // 1. Time slot conflicts (same lecturer at same time)
        // 2. Venue conflicts (same venue at same time)
        // 3. Preference violations (e.g., early morning classes)
        // 4. Constraint violations

        cost += calculateConflictCost(schedule);
        cost += calculatePreferenceCost(schedule);
        cost += calculateConstraintCost(schedule);

        return cost;
    }

    private double calculateConflictCost(List<ScheduleEntry> schedule) {
        double cost = 0.0;

        for (int i = 0; i < schedule.size(); i++) {
            for (int j = i + 1; j < schedule.size(); j++) {
                ScheduleEntry entry1 = schedule.get(i);
                ScheduleEntry entry2 = schedule.get(j);

                // Same time slot conflict
                if (entry1.getTimeSlot().getId().equals(entry2.getTimeSlot().getId())) {

                    // 🔁 Lecturer conflict
                    Set<Long> lecturerIds1 = entry1.getCourseAssignment().getLecturers().stream()
                            .map(Lecturer::getId)
                            .collect(Collectors.toSet());

                    Set<Long> lecturerIds2 = entry2.getCourseAssignment().getLecturers().stream()
                            .map(Lecturer::getId)
                            .collect(Collectors.toSet());

                    // Intersection = conflict
                    lecturerIds1.retainAll(lecturerIds2);
                    if (!lecturerIds1.isEmpty()) {
                        cost += 1000; // 🔥 High penalty for lecturer clash
                    }

                    if (entry1.getVenue().getId().equals(entry2.getVenue().getId())) {
                        cost += 1000; // 🔥 High penalty for venue clash
                    }
                }
            }
        }

        return cost;
    }


    private double calculatePreferenceCost(List<ScheduleEntry> schedule) {
        double cost = 0.0;

        for (ScheduleEntry entry : schedule) {
            // Prefer afternoon slots for some courses
            var startTime = entry.getTimeSlot().getStartTime();
            if (startTime.isBefore(java.time.LocalTime.of(9, 0))) {
                cost += 10; // Small penalty for very early classes
            }
        }

        return cost;
    }

    private double calculateConstraintCost(List<ScheduleEntry> schedule) {
        double cost = 0.0;

        for (ScheduleEntry entry : schedule) {
            var course = entry.getCourseAssignment().getCourse();

            // DLD constraint violations
            if (course.getCode().toUpperCase().contains("DLD")) {
                var dayOfWeek = entry.getTimeSlot().getDayOfWeek();
                if (dayOfWeek != java.time.DayOfWeek.TUESDAY &&
                        dayOfWeek != java.time.DayOfWeek.THURSDAY) {
                    cost += 500; // High penalty for DLD day violation
                }

                var startTime = entry.getTimeSlot().getStartTime();
                if (!startTime.equals(java.time.LocalTime.of(12, 0))) {
                    cost += 500; // High penalty for DLD time violation
                }
            }
        }

        return cost;
    }
}
