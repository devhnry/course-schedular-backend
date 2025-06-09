package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.models.schedule.TimeSlot;
import com.henry.universitycourseschedular.repositories.TimeSlotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TimeSlotSeeder {

    private final TimeSlotRepository timeSlotRepository;

    @PostConstruct
    public void seed() {
        if (timeSlotRepository.count() > 0) return; // Prevent re-seeding

        List<TimeSlot> timeSlots = new ArrayList<>();

        // Define a fixed set of time intervals (e.g., 1-hour slots)
        LocalTime[] starts = {
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0),
                LocalTime.of(17, 0),
                LocalTime.of(18, 0),
                LocalTime.of(19, 0)
        };

        for (DayOfWeek day : DayOfWeek.values()) {
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) continue; // skip weekends

            for (LocalTime start : starts) {
                timeSlots.add(TimeSlot.builder()
                        .startTime(start)
                        .endTime(start.plusHours(1))
                        .dayOfWeek(day)
                        .build());
            }
        }
        timeSlotRepository.saveAll(timeSlots);
    }
}
