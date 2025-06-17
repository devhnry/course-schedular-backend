package com.henry.universitycourseschedular.models.course;

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
public class CourseAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lecturer lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    private boolean isGeneral; // e.g. GST course
}
