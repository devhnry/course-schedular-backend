package com.henry.universitycourseschedular.models.schedule;

import com.henry.universitycourseschedular.models.core.Venue;
import com.henry.universitycourseschedular.models.course.CourseAssignment;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CourseAssignment courseAssignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TimeSlot timeSlot;

    // The actual venue assigned during scheduling
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;
}
