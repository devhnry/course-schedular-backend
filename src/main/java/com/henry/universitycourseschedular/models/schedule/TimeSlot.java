package com.henry.universitycourseschedular.models.schedule;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    @Override
    public String toString() {
        return startTime + " - " + endTime;
    }
}