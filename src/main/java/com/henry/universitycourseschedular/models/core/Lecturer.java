package com.henry.universitycourseschedular.models.core;

import com.henry.universitycourseschedular.enums.Title;
import com.henry.universitycourseschedular.models.course.Course;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Title title;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    // One lecturer can have many courses.
    @OneToMany(mappedBy = "lecturer", fetch = FetchType.LAZY)
    private List<Course> courses;
}