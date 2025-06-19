package com.henry.universitycourseschedular.models;

import com.henry.universitycourseschedular.enums.Semester;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AcademicSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Department department;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleEntry> entries;

    private boolean isFinalized;
    private int version;
    private LocalDateTime generatedAt;
}
