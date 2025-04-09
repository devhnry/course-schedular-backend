package com.henry.universitycourseschedular.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lecturer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    private String name;

    // One lecturer can have many courses.
    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;

    // Many lecturers may have the same available timeslot and vice versa.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "lecturer_timeslots",
            joinColumns = @JoinColumn(name = "lecturer_id"),
            inverseJoinColumns = @JoinColumn(name = "timeslot_id"))
    private Set<TimeSlot> availableSlots;
}
