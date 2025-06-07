package com.henry.universitycourseschedular.models.schedule;

import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.user.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
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

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private String session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Department department;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleEntry> entries;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser generatedBy;

    private boolean isFinalized;
    private int version;
    private ZonedDateTime generatedAt;
}
