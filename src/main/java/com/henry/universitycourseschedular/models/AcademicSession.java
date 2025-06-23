package com.henry.universitycourseschedular.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sessionLabel; // e.g., "2024/2025"

    private int startYear;
    private int endYear;

    public static AcademicSession fromStartYear(int year) {
        return AcademicSession.builder()
                .sessionLabel(year + "/" + (year + 1))
                .startYear(year)
                .endYear(year + 1)
                .build();
    }

    @Override
    public String toString() {
        return sessionLabel;
    }
}

