package com.henry.universitycourseschedular.models.course;

import com.henry.universitycourseschedular.enums.CourseType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Program program;

    private int expectedStudents;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;
}