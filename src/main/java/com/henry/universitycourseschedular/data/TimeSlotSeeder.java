package com.henry.universitycourseschedular.data;

import com.henry.universitycourseschedular.models.schedule.TimeSlot;
import com.henry.universitycourseschedular.repositories.TimeSlotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Component @Slf4j
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

        ZoneId zoneId = ZoneId.systemDefault(); // or use ZoneId.of("Africa/Lagos")
        LocalDate today = LocalDate.now();

        for (DayOfWeek day : DayOfWeek.values()) {
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) continue; // skip weekends

            for (LocalTime start : starts) {
                ZonedDateTime startTime = ZonedDateTime.of(today, start, zoneId);
                ZonedDateTime endTime = startTime.plusHours(1);

                timeSlots.add(TimeSlot.builder()
                        .startTime(startTime)
                        .endTime(endTime)
                        .dayOfWeek(day)
                        .build());
            }
        }
        timeSlotRepository.saveAll(timeSlots);

        log.info("✅ Seeded TimeSlot.");
    }
}
