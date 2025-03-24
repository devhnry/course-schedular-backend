package com.henry.universitycourseschedular.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapping a Map with Course as key and TimeSlot as value.
    // This example uses a join table to associate courses with time slots.
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "schedule_entries",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    @MapKeyJoinColumn(name = "course_id")
    private Map<Course, TimeSlot> courseSchedule;
}
