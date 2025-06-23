package com.henry.universitycourseschedular.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_code", referencedColumnName = "code")
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_code", referencedColumnName = "code")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_name", referencedColumnName = "name")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code", referencedColumnName = "code", nullable = false)
    private Course course;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_assignment_lecturers",
            joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "lecturers")
    )
    private List<Lecturer> lecturers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_code", referencedColumnName = "code")
    private CollegeBuilding collegeBuilding;

    public CollegeBuilding getEffectiveBuilding() {
        return this.collegeBuilding != null
                ? this.collegeBuilding
                : this.department.getCollegeBuilding();
    }

}
