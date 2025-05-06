package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.enums.Semester;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class CourseAssignment {
    @Id
    private Long id;

    @ManyToOne
    private Course course;

    @ManyToOne
    private Lecturer lecturer;

    @ManyToOne
    private TimeSlot timeSlot;

    @ManyToOne
    private Venue venue;

    private Semester semester;
}
