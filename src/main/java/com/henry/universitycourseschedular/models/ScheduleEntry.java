package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @Enumerated(EnumType.STRING)
    private TimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;
}
