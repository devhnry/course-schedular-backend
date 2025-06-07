package com.henry.universitycourseschedular.models.course;

import com.henry.universitycourseschedular.enums.CourseType;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.core.Lecturer;
import com.henry.universitycourseschedular.models.core.Program;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private int credits;

    // Many courses can be taught by one lecturer.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Department department;

    private int expectedStudents;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;
}